package com.maxy.caller.admin.worker;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.maxy.caller.admin.cache.CacheService;
import com.maxy.caller.admin.config.AdminConfigCenter;
import com.maxy.caller.admin.enums.RouterStrategyEnum;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.timer.CacheTimer;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.dto.ResultDTO;
import com.maxy.caller.dto.RpcRequestDTO;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_SUCCEED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXPIRED;
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

    private volatile boolean toggle = true;
    @Resource
    private AdminConfigCenter adminConfigCenter;
    @Resource
    private NettyServerHelper nettyServerHelper;
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService worker = threadPoolConfig.getSingleThreadExecutor(true);
    private ScheduledExecutorService scheduledExecutor = threadPoolConfig.getPublicScheduledExecutor(true);
    private ExecutorService executorService = threadPoolConfig.getPublicScheduledExecutor(true);
    private ListeningExecutorService listeningWorker = MoreExecutors.listeningDecorator(executorService);
    private CacheTimer cacheTimer = CacheTimer.getInstance();

    @Override
    public void start() {
        worker.execute(() -> {
            while (toggle) {
                try {
                    pop();
                    //打散时间
                    TimeUnit.DAYS.sleep(1);
                } catch (Exception e) {
                    log.error("队列获取数据出现异常", e);
                }
            }
        });
    }

    private void pop() {
        try {
            int size = cacheService.getNodeMap().size();
            //获取索引列表
            Date currentDate = new Date();
            for (int i = 0, length = size / 2; i < length; i++) {
                List<String> keys = Lists.newArrayList(DICTIONARY_INDEX_FORMAT.join(i));
                List<String> args = Arrays.asList("10",
                                                  String.valueOf(DateUtils.addSecond(currentDate, -1).getTime()),
                                                  String.valueOf(DateUtils.addDays(currentDate, 5).getTime()),
                                                  "LIMIT", "0", adminConfigCenter.getLimitNum());
                List<Object> queueData = cacheService.getQueueData(keys, args);
                queueData.forEach(element -> {
                    List<String> values = (List<String>) element;
                });
            }
         /*   for (int i = 0, length = cacheService.getNodeMap().size() / 2; i < length; i++) {
                String key = "";
                for (Object element : indexData) {
                    key = element + ":{" + i + "}";
                    //获取参数
                    Date currentDate = new Date();
                    List<String> args = Arrays.asList(String.valueOf(DateUtils.addSecond(currentDate, -1).getTime()),
                            String.valueOf(DateUtils.addDays(currentDate, 5).getTime()),
                            "LIMIT", "0", adminConfigCenter.getLimitNum());
                    //获取队列数据[0]索引数组
                    List<Object> queueData = cacheService.getQueueData(ImmutableList.of(key), args);
                    if (CollectionUtils.isEmpty(queueData)) {
                        log.warn("pop#队列上没有找到要处理的数据!");
                        return;
                    }
                    //循环执行
                    invokeAll(queueData);
                }
            }*/
        } catch (Exception e) {
            log.error("pop#执行出队时发现异常!!", e);
        }
    }

    /**
     * 检查过期任务
     *
     * @param taskDetailInfoBO
     * @return
     */
    private boolean checkExpireTaskInfo(TaskDetailInfoBO taskDetailInfoBO) {
        if (taskDetailInfoBO.getExecutionTime().getTime() <= System.currentTimeMillis()) {
            log.info("checkExpireTaskInfo#[{}]任务,时间:[{}]已过期将丢弃.", getUniqueName(taskDetailInfoBO), taskDetailInfoBO.getExecutionTime());
            taskDetailInfoBO.setExecutionStatus(EXPIRED.getCode());
            taskDetailInfoService.update(taskDetailInfoBO);
            return true;
        }
        return false;
    }

    /**
     * 执行时间轮
     *
     * @param queueData
     */
    private void invokeAll(List<Object> queueData) throws InterruptedException {
        for (int i = 1; i < queueData.size(); i++) {
            List<String> zSetList = (List<String>) queueData.get(i);
            for (String element : zSetList) {
                TaskDetailInfoBO taskDetailInfoBO = JSONUtils.parseObject(element, TaskDetailInfoBO.class);
                if (checkExpireTaskInfo(taskDetailInfoBO)) {
                    continue;
                }
                cacheTimer.newTimeout(timeout -> {
                    remoteClientMethod(taskDetailInfoBO);
                }, taskDetailInfoBO.getExecutionTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 缓存路由策略
     *
     * @param taskDetailInfoBO
     * @return
     */
    private Byte getExecutorRouterStrategy(TaskDetailInfoBO taskDetailInfoBO) {
        String strategyName = cacheService.get(getUniqueName(taskDetailInfoBO));
        if (StringUtils.isBlank(strategyName)) {
            TaskBaseInfoBO taskBaseInfoBO = taskBaseInfoService.getByUniqueKey(taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey(), taskDetailInfoBO.getTopic());
            cacheService.set(getGroupName(taskBaseInfoBO), 3600, taskBaseInfoBO.getExecutorRouterStrategy().toString());
            return taskBaseInfoBO.getExecutorRouterStrategy();
        }
        return Byte.valueOf(strategyName);
    }


    /**
     * 通过netty调用远程方法
     *
     * @param taskDetailInfoBO
     */
    private void remoteClientMethod(TaskDetailInfoBO taskDetailInfoBO) {
        //netty call client
        listeningWorker.execute(() -> {
            List<Channel> channels = nettyServerHelper.getActiveChannel().get(TriggerWorker.this.getUniqueName(taskDetailInfoBO));
            if (CollectionUtils.isEmpty(channels)) {
                return;
            }
            String remoteAddr = RouterStrategyEnum.get(TriggerWorker.this.getExecutorRouterStrategy(taskDetailInfoBO), parse(channels));
            //调用远程触发客户端
            if (CollectionUtils.isNotEmpty(channels)) {
                CallerTaskDTO callerTaskDTO = new CallerTaskDTO();
                BeanUtils.copyProperties(taskDetailInfoBO, callerTaskDTO);
                Channel channel = nettyServerHelper.getChannelByAddr(remoteAddr);
                channel.writeAndFlush(ProtocolMsg.toEntity(callerTaskDTO)).addListener(future -> {
                    processResult(taskDetailInfoBO, future);
                });
            }
        });
    }

    /**
     * 处理返回结果
     *
     * @param taskDetailInfoBO
     * @param future
     */
    private void processResult(TaskDetailInfoBO taskDetailInfoBO, Future<? super Void> future) {
        ProtocolMsg protocolMsg = null;
        try {
            log.info("processResult#执行参数:{}", taskDetailInfoBO);
            protocolMsg = (ProtocolMsg) future.get(taskDetailInfoBO.getTimeout(), TimeUnit.MILLISECONDS);
            log.info("processResult#执行客户端方法返回值:{}", protocolMsg);
        } catch (Exception e) {
            log.error("调用方法超时或者遇到异常！参数：{}", taskDetailInfoBO, e);
        }
        if (Objects.isNull(protocolMsg)) {
            return;
        }
        RpcRequestDTO request = (RpcRequestDTO) protocolMsg.getBody();
        ResultDTO resultDTO = request.getResultDTO();
        taskDetailInfoBO.setExecutionStatus(resultDTO.isSuccess() ? EXECUTION_SUCCEED.getCode() : EXECUTION_FAILED.getCode());
        taskDetailInfoService.update(taskDetailInfoBO);
    }

    @Override
    public void stop() {
        toggle = false;
        ThreadPoolRegisterCenter.destroy(worker, scheduledExecutor, listeningWorker, executorService);
    }
}
