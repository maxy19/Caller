package com.maxy.caller.admin.worker;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.common.RpcFuture;
import com.maxy.caller.core.config.GeneralConfigCenter;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.exception.BusinessException;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.timer.CacheTimer;
import com.maxy.caller.core.utils.CallerUtils;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.Value;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

import static com.maxy.caller.admin.enums.RouterStrategyEnum.router;
import static com.maxy.caller.core.common.RpcHolder.REQUEST_MAP;
import static com.maxy.caller.core.constant.ThreadConstant.ADMIN_TRIGGER_BACKUP_WORKER_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.ADMIN_TRIGGER_WORKER_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.INVOKE_CLIENT_TASK_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.POP_LOOP_SLOT_THREAD_POOL;
import static com.maxy.caller.core.constant.ThreadConstant.RETRY_TASK_THREAD_POOL;
import static com.maxy.caller.core.enums.ExceptionEnum.FOUND_NOT_EXECUTE_INFO;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_SUCCEED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXPIRED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.RETRYING;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DETAIL_TASK_INFO;
import static com.maxy.caller.core.enums.GenerateKeyEnum.LIST_QUEUE_FORMAT_BACKUP;
import static com.maxy.caller.core.enums.GenerateKeyEnum.ZSET_QUEUE_FORMAT;
import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * 从队列里面获取数据调用netty-rpc执行
 *
 * @Author maxy
 **/
@Component
@Log4j2
public class TriggerWorker implements AdminWorker {

    @Resource
    private CacheService cacheService;
    @Resource
    private TaskDetailInfoService taskDetailInfoService;
    @Resource
    private TaskBaseInfoService taskBaseInfoService;
    @Resource
    private TaskLogService taskLogService;
    @Resource
    private GeneralConfigCenter config;
    @Resource
    private NettyServerHelper nettyServerHelper;
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService worker = threadPoolConfig.getSingleThreadExecutor(true, ADMIN_TRIGGER_WORKER_THREAD_POOL);
    private ExecutorService backupWorker = threadPoolConfig.getSingleThreadExecutor(false, ADMIN_TRIGGER_BACKUP_WORKER_THREAD_POOL);
    private ExecutorService executorSchedule = threadPoolConfig.getPublicThreadPoolExecutor(false, INVOKE_CLIENT_TASK_THREAD_POOL);
    private CacheTimer cacheTimer = CacheTimer.getEntity();
    private ExecutorService retryExecutor = threadPoolConfig.getPublicThreadPoolExecutor(false, RETRY_TASK_THREAD_POOL);
    private ExecutorService loopSlotExecutor = threadPoolConfig.getPublicThreadPoolExecutor(true, POP_LOOP_SLOT_THREAD_POOL);
    private volatile boolean toggle = true;

    @PostConstruct
    public void init() {
        backupWorker.execute(() -> {
            for (int slot = 0, length = config.getTotalSlot(); slot < length; slot++) {
                List<String> keys = Lists.newArrayList(LIST_QUEUE_FORMAT_BACKUP.join(slot));
                List<String> tasks = cacheService.getQueueDataByBackup(keys, ImmutableList.of(config.getLimitNum()));
                if (CollectionUtils.isEmpty(tasks)) {
                    continue;
                }
                invoke(tasks);
            }
        });
    }

    @Override
    public void start() {
        worker.execute(() -> {
            while (toggle) {
                try {
                    pop();
                    //打散时间
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (Exception e) {
                    log.error("队列获取数据出现异常", e);
                }
            }
        });
    }

    private void pop() {
        try {
            //获取索引列表
            for (int slot = 0, length = config.getTotalSlot(); slot < length; slot++) {
                Value<Integer> indexValue = new Value<>(slot);
                loopSlotExecutor.execute(() -> {
                    getAndInvokeAll(indexValue.getValue());
                });
            }
        } catch (Exception e) {
            log.error("pop#执行出队时发现异常!!", e);
        }
    }

    /**
     * lua执行redis获取数据
     *
     * @param slot
     * @return
     */
    private void getAndInvokeAll(int slot) {
        int result;
        do {
            result = 0;
            List<String> keys = Lists.newArrayList(ZSET_QUEUE_FORMAT.join(slot), LIST_QUEUE_FORMAT_BACKUP.join(slot));
            Long now = System.currentTimeMillis();
            String start = String.valueOf(now - TEN_MINUTE_OF_SECOND);
            String end = String.valueOf(now + TEN_MINUTE_OF_SECOND);
            List<String> args = Arrays.asList(start, end, "LIMIT", "0", config.getLimitNum());
            List<Object> queueData = cacheService.getQueueData(keys, args);
            if (CollectionUtils.isNotEmpty(queueData)) {
                log.info("找到数据!!,槽位:{}.", queueData);
                invokeAll(queueData);
            }
            result += queueData.size();
        } while (result > 0);
    }

    /**
     * 检查过期任务
     *
     * @param context
     * @return
     */
    private boolean checkExpireTaskInfo(Pair<TaskDetailInfoBO, CallerTaskDTO> context) {
        TaskDetailInfoBO taskDetailInfoBO = context.getLeft();
        if (taskDetailInfoBO.getExecutionTime().getTime() <= System.currentTimeMillis()) {
            log.warn("checkExpireTaskInfo#[{}]任务,时间:[{}]已过期将丢弃.", getUniqueName(taskDetailInfoBO), taskDetailInfoBO.getExecutionTime());
            taskDetailInfoService.removeBackup(taskDetailInfoBO);
            //更新为过期
            if (taskDetailInfoService.updateStatus(taskDetailInfoBO.getId(), EXPIRED.getCode())) {
                taskLogService.save(taskDetailInfoBO);
            }
            return true;
        }
        return false;
    }

    /**
     * 执行时间轮
     *
     * @param queueData
     */
    private void invokeAll(List<Object> queueData) {
        for (int i = 0, length = queueData.size(); i < length; i++) {
            List<String> list = (List<String>) queueData.get(i);
            log.info("当前槽位数据量:{}", list.size());
            invoke(list);
        }
    }

    /**
     * 加入时间轮
     *
     * @param list
     */
    private void invoke(List<String> list) {
        for (String element : list) {
            TaskDetailInfoBO detailInfoBO = JSONUtils.parseObject(element, TaskDetailInfoBO.class);
            CallerTaskDTO dto = new CallerTaskDTO().fullField(detailInfoBO);
            Pair<TaskDetailInfoBO, CallerTaskDTO> context = Pair.of(detailInfoBO, dto);
            if (checkExpireTaskInfo(context)) {
                continue;
            }
            log.debug("invoke#[{}]放入定时轮.", dto.getDetailTaskId());
            cacheTimer.newTimeout(timeout -> {
                remoteClientMethod(context);
            }, dto.getExecutionTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private TaskDetailInfoBO getDetailTask(CallerTaskDTO callerTaskDTO) {
        String value = cacheService.get(DETAIL_TASK_INFO.join(callerTaskDTO.getDetailTaskId()));
        if (StringUtils.isBlank(value)) {
            return taskDetailInfoService.getByInfoId(callerTaskDTO.getDetailTaskId());
        }
        return JSONUtils.parseObject(value, TaskDetailInfoBO.class);
    }


    /**
     * 通过netty调用远程方法
     *
     * @param context
     */
    private void remoteClientMethod(Pair<TaskDetailInfoBO, CallerTaskDTO> context) {
        //netty call client
        executorSchedule.execute(() -> {
            List<Channel> channels = nettyServerHelper.getActiveChannel().get(getGroupName(context.getLeft()));
            if (CollectionUtils.isEmpty(channels)) {
                log.error("remoteClientMethod#没有找到可以连接的通道!!!!.");
                return;
            }
            //获取channel
            Channel channel = getChannel(context.getRight(), channels);
            if (Objects.isNull(channel)) {
                log.error("remoteClientMethod#根据{}没有找到要发送的通道,请检查客户端是否存在!", context.getRight());
                return;
            }
            boolean result = syncCallback(context.getRight(), channel);
            //发现异常如果需要重试将再次调用方法
            if (!result) {
                if (context.getRight().getRetryNum() > 0) {
                    retryExecutor.execute(() -> retryExecute(context, channel));
                } else {
                    taskDetailInfoService.removeBackup(context.getLeft());
                    context.getLeft().fillStatusAndErrorMsg("执行远程方法异常!", EXECUTION_FAILED.getCode());
                    taskDetailInfoService.update(context.getLeft());
                    taskLogService.save(context.getLeft(), parse(channel), (byte) 0);
                }
            }
        });
    }

    /**
     * 获取channel
     *
     * @param callerTaskDTO
     * @param channels
     * @return
     */
    private Channel getChannel(CallerTaskDTO callerTaskDTO, List<Channel> channels) {
        return nettyServerHelper.getChannelByAddr(
                router(taskBaseInfoService.getRouterStrategy(callerTaskDTO.getGroupKey(),
                        callerTaskDTO.getBizKey(),
                        callerTaskDTO.getTopic()),
                        parse(channels)));
    }

    /**
     * 重试
     */
    private void retryExecute(Pair<TaskDetailInfoBO, CallerTaskDTO> context, Channel channel) {
        for (byte retryNum = 1, totalNum = context.getRight().getRetryNum(); retryNum <= totalNum; retryNum++) {
            boolean result = syncCallback(context.getRight(), channel);
            context.getLeft().setExecutionStatus(RETRYING.getCode());
            taskLogService.save(context.getLeft(), parse(channel), retryNum);
            if (!result) {
                taskDetailInfoService.removeBackup(context.getLeft());
                taskDetailInfoService.update(context.getLeft());
                return;
            }
        }
    }


    /**
     * 同步执行回调
     *
     * @return
     */
    private boolean syncCallback(CallerTaskDTO callerTaskDTO, Channel channel) {
        Value<Boolean> value = new Value<>(true);
        RpcFuture<ProtocolMsg> rpcFuture = new RpcFuture(new DefaultPromise(new DefaultEventExecutor()), callerTaskDTO.getTimeout());
        ProtocolMsg request = ProtocolMsg.toEntity(callerTaskDTO);
        //request
        boolean result = send(channel, value, request);
        if (!result) {
            return false;
        }
        //callback
        try {
            REQUEST_MAP.put(request.getRequestId(), rpcFuture);
            Stopwatch stopwatch = Stopwatch.createStarted();
            ProtocolMsg protocolMsg = rpcFuture.getPromise().get(rpcFuture.getTimeout(), TimeUnit.MILLISECONDS);
            handleCallback.accept(protocolMsg, channel);
            log.info("syncCallback:任务ID:{},耗时:{}", callerTaskDTO.getDetailTaskId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            value.setValue(false);
            log.error("syncCallback#调用方法超时或者遇到异常!! 任务ID:{}", callerTaskDTO.getDetailTaskId(), e);
        }
        return value.getValue();
    }

    /**
     * 写入并发送
     *
     * @param channel
     * @param value
     * @param request
     * @return
     */
    @SneakyThrows
    private boolean send(Channel channel, Value<Boolean> value, ProtocolMsg request) {
        ChannelFuture channelFuture = null;
        if (CallerUtils.isChannelWritable(channel)) {
            channelFuture = channel.writeAndFlush(request);
        } else {
            channelFuture = channel.writeAndFlush(request).sync();
        }
        value.setValue(CallerUtils.monitor(channelFuture));
        return value.getValue();
    }

    /**
     * callback 处理
     */
    private BiConsumer<ProtocolMsg, Channel> handleCallback = ((response, channel) -> {
        RpcRequestDTO request = (RpcRequestDTO) getRequest(response);
        CallerTaskDTO dto = request.getCallerTaskDTO();
        TaskDetailInfoBO taskDetailInfoBO = getDetailTask(dto);
        if (Objects.isNull(taskDetailInfoBO)) {
            throw new BusinessException(FOUND_NOT_EXECUTE_INFO);
        }
        taskDetailInfoService.removeBackup(taskDetailInfoBO);
        ResultDTO resultDTO = request.getResultDTO();
        taskDetailInfoBO.setExecutionStatus(resultDTO.isSuccess() ? EXECUTION_SUCCEED.getCode() : EXECUTION_FAILED.getCode());
        taskDetailInfoBO.setErrorMsg(resultDTO.getMessage());
        taskDetailInfoService.update(taskDetailInfoBO);
        taskLogService.saveClientResult(taskDetailInfoBO, resultDTO.getMessage(), parse(channel));
    });

    private Object getRequest(ProtocolMsg protocolMsg) {
        Object request = protocolMsg.getBody();
        Preconditions.checkArgument(Objects.nonNull(request));
        return request;
    }

    @Override
    public void stop() {
        toggle = false;
        ThreadPoolRegisterCenter.destroy(backupWorker, retryExecutor, executorSchedule, worker);
    }

}
