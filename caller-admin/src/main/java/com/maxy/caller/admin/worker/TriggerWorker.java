package com.maxy.caller.admin.worker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.config.AdminConfigCenter;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.common.RpcFuture;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.exception.BusinessException;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.timer.CacheTimer;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.pojo.Value;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
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
import static com.maxy.caller.core.enums.ExceptionEnum.FOUND_NOT_EXECUTE_INFO;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_SUCCEED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXPIRED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.RETRYING;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DICTIONARY_INDEX_BACKUP_FORMAT;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DICTIONARY_INDEX_FORMAT;
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
    private AdminConfigCenter adminConfigCenter;
    @Resource
    private NettyServerHelper nettyServerHelper;
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService worker = threadPoolConfig.getSingleThreadExecutor(true);
    private ExecutorService backupWorker = threadPoolConfig.getSingleThreadExecutor(true);
    private ExecutorService executorSchedule = threadPoolConfig.getPublicThreadPoolExecutor(true);
    private CacheTimer cacheTimer = CacheTimer.getInstance();
    private volatile boolean toggle = true;

    @PostConstruct
    public void init() {
        backupWorker.execute(() -> {
            int size = cacheService.getNodeMap().size();
            for (int i = 0; i < size / 2; i++) {
                List<String> keys = Lists.newArrayList(DICTIONARY_INDEX_BACKUP_FORMAT.join(i));
                List<String> tasks = cacheService.getQueueDataByBackup(keys, ImmutableList.of("1000"));
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
                    TimeUnit.MILLISECONDS.sleep(10 * RandomUtils.nextInt(1, 100));
                } catch (Exception e) {
                    log.error("队列获取数据出现异常", e);
                }
            }
        });
    }

    private void pop() {
        try {
            //获取索引列表
            for (int index = 0, length = cacheService.getNodeMap().size() / 2; index < length; index++) {
                List<Object> queueData = getQueueData(index);
                if (CollectionUtils.isEmpty(queueData)) {
                    continue;
                }
                //循环执行
                log.info("找到数据!!,槽位:{}", index);
                invokeAll(queueData);
            }
        } catch (Exception e) {
            log.error("pop#执行出队时发现异常!!", e);
        }
    }

    /**
     * lua执行redis获取数据
     *
     * @param index
     * @return
     */
    private List<Object> getQueueData(int index) {
        List<String> keys = Lists.newArrayList(DICTIONARY_INDEX_FORMAT.join(index));
        List<String> args = Arrays.asList("100",//length
                "-inf", "+inf",
                "LIMIT", "0", adminConfigCenter.getLimitNum());
        return cacheService.getQueueData(keys, args);
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
            taskDetailInfoBO.setExecutionStatus(EXPIRED.getCode());
            taskDetailInfoService.update(taskDetailInfoBO);
            taskDetailInfoService.removeBackup(context.getRight());
            taskLogService.save(taskDetailInfoBO);
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
            CallerTaskDTO callerTaskDTO = JSONUtils.parseObject(element, CallerTaskDTO.class);
            TaskDetailInfoBO taskDetailInfoBO = taskDetailInfoService.getByInfoId(callerTaskDTO.getDetailTaskId());
            Pair<TaskDetailInfoBO, CallerTaskDTO> context = Pair.of(taskDetailInfoBO, callerTaskDTO);
            if (checkExpireTaskInfo(context)) {
                continue;
            }
            log.info("invoke#[{}]放入定时轮.", element);
            cacheTimer.newTimeout(timeout -> {
                remoteClientMethod(context);
            }, callerTaskDTO.getExecutionTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
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
                log.error("没有找打可以连接的通道!!!!.");
                return;
            }
            //获取channel
            Channel channel = getChannel(context.getRight(), channels);
            //执行并监听结果
            boolean result = syncCallback(context.getRight(), channel);
            //发现异常如果需要重试将再次调用方法
            if (result) {
                if (context.getLeft().getRetryNum() > 0) {
                    retryExecute(context, channel);
                } else {
                    context.getLeft().fillStatusAndErrorMsg("执行远程方法异常!", EXECUTION_FAILED.getCode());
                    taskDetailInfoService.update(context.getLeft());
                    taskDetailInfoService.removeBackup(context.getRight());
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
        for (byte retryNum = 0, totalNum = context.getRight().getRetryNum(); retryNum < totalNum; retryNum++) {
            boolean result = syncCallback(context.getRight(), channel);
            if (result) {
                context.getLeft().setExecutionStatus(RETRYING.getCode());
                taskDetailInfoService.update(context.getLeft());
                taskDetailInfoService.removeBackup(context.getRight());
                taskLogService.save(context.getLeft(), parse(channel), retryNum);
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
        Value<Boolean> value = new Value<>(false);
        RpcFuture<ProtocolMsg> rpcFuture = new RpcFuture(new DefaultPromise(new DefaultEventExecutor()), callerTaskDTO.getTimeout());
        ProtocolMsg request = ProtocolMsg.toEntity(callerTaskDTO);
        //request
        Value<Boolean> result = send(channel, value, request);
        if (result.getValue()) {
            return result.getValue();
        }
        //callback
        try {
            log.info("syncCallback#执行参数:{}", callerTaskDTO);
            REQUEST_MAP.put(request.getRequestId(), rpcFuture);
            handleCallback.accept(rpcFuture.getPromise().get(rpcFuture.getTimeout(), TimeUnit.MILLISECONDS), channel);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            value.setValue(true);
            log.error("syncCallback#调用方法超时或者遇到异常！参数:{}", callerTaskDTO, e);
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
    private Value<Boolean> send(Channel channel, Value<Boolean> value, ProtocolMsg request) {
        channel.writeAndFlush(request).addListener(future -> {
            if (future.isSuccess()) {
                log.info("send#[{}]发送成功!!!!", request.getRequestId());
            } else {
                value.setValue(true);
                log.error("send#发送失败,出现异常:{}", future.cause().getMessage());
            }
        });
        return value;
    }

    /**
     * callback 处理
     */
    private BiConsumer<ProtocolMsg, Channel> handleCallback = ((response, channel) -> {
        RpcRequestDTO request = (RpcRequestDTO) getRequest(response);
        CallerTaskDTO dto = request.getCallerTaskDTO();
        TaskDetailInfoBO taskDetailInfoBO = taskDetailInfoService.getByInfoId(dto.getDetailTaskId());
        if (Objects.isNull(taskDetailInfoBO)) {
            throw new BusinessException(FOUND_NOT_EXECUTE_INFO);
        }
        ResultDTO resultDTO = request.getResultDTO();
        taskDetailInfoBO.setExecutionStatus(resultDTO.isSuccess()
                ? EXECUTION_SUCCEED.getCode() : EXECUTION_FAILED.getCode());
        taskDetailInfoBO.setErrorMsg(resultDTO.getMessage());
        taskDetailInfoService.update(taskDetailInfoBO);
        taskDetailInfoService.removeBackup(dto);
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
        ThreadPoolRegisterCenter.destroy(backupWorker, worker, executorSchedule);
    }

}
