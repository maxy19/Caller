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
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.timer.CacheTimer;
import com.maxy.caller.dto.CallerTaskDTO;
import com.maxy.caller.pojo.Value;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXPIRED;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.RETRYING;
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

    private volatile boolean toggle = true;
    @Resource
    private AdminConfigCenter adminConfigCenter;
    @Resource
    private NettyServerHelper nettyServerHelper;
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService worker = threadPoolConfig.getSingleThreadExecutor(true);
    private ScheduledExecutorService scheduledExecutor = threadPoolConfig.getPublicScheduledExecutor(true);
    private ExecutorService executorService = threadPoolConfig.getPublicScheduledExecutor(true);
    private ExecutorService retryService = threadPoolConfig.getPublicScheduledExecutor(true);
    private ListeningExecutorService listeningWorker = MoreExecutors.listeningDecorator(executorService);
    private CacheTimer cacheTimer = CacheTimer.getInstance();

    @Override
    public void start() {
        worker.execute(() -> {
            while (toggle) {
                try {
                    pop();
                    //打散时间
                    TimeUnit.SECONDS.sleep(10);
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
            for (int index = 0, length = size / 2; index < length; index++) {
                //循环执行
                invokeAll(getQueueData(currentDate, index));
            }
        } catch (Exception e) {
            log.error("pop#执行出队时发现异常!!", e);
        }
    }

    /**
     * lua执行redis获取数据
     *
     * @param currentDate
     * @param index
     * @return
     */
    private List<Object> getQueueData(Date currentDate, int index) {
        List<String> keys = Lists.newArrayList(DICTIONARY_INDEX_FORMAT.join(index));
        List<String> args = Arrays.asList("10",//length
                String.valueOf(DateUtils.addSecond(currentDate, -1).getTime()),
                String.valueOf(DateUtils.addDays(currentDate, 5).getTime()),
                "LIMIT", "0", adminConfigCenter.getLimitNum());
        return cacheService.getQueueData(keys, args);
    }

    /**
     * 检查过期任务
     *
     * @param taskDetailInfoBO
     * @return
     */
    private boolean checkExpireTaskInfo(TaskDetailInfoBO taskDetailInfoBO) {
        if (taskDetailInfoBO.getExecutionTime().getTime() <= System.currentTimeMillis()) {
            log.info("checkExpireTaskInfo#[{}]任务,时间:[{}]已过期将丢弃.", (taskDetailInfoBO), taskDetailInfoBO.getExecutionTime());
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
    private void invokeAll(List<Object> queueData) {
        for (int i = 0, length = queueData.size(); i < length; i++) {
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
        String strategyValue = cacheService.hget(getUniqueName(taskDetailInfoBO), STRATEGY_VALUE);
        if (StringUtils.isBlank(strategyValue)) {
            TaskBaseInfoBO taskBaseInfoBO = taskBaseInfoService.getByUniqueKey(taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey(), taskDetailInfoBO.getTopic());
            cacheService.hmset(getUniqueName(taskBaseInfoBO), STRATEGY_VALUE, String.valueOf(taskBaseInfoBO.getExecutorRouterStrategy()), ONE_HOUR);
            return taskBaseInfoBO.getExecutorRouterStrategy();
        }
        return Byte.valueOf(strategyValue);
    }


    /**
     * 通过netty调用远程方法
     *
     * @param taskDetailInfoBO
     */
    private void remoteClientMethod(TaskDetailInfoBO taskDetailInfoBO) {
        //netty call client
        listeningWorker.execute(() -> {
            List<Channel> channels = nettyServerHelper.getActiveChannel().get(getGroupName(taskDetailInfoBO));
            if (CollectionUtils.isEmpty(channels)) {
                return;
            }
            //调用远程触发客户端
            if (CollectionUtils.isNotEmpty(channels)) {
                CallerTaskDTO callerTaskDTO = new CallerTaskDTO();
                BeanUtils.copyProperties(taskDetailInfoBO, callerTaskDTO);
                //获取channel
                Channel channel = nettyServerHelper.getChannelByAddr(RouterStrategyEnum.get(getExecutorRouterStrategy(taskDetailInfoBO), parse(channels)));
                //执行并监听结果
                boolean result = executionSchedule(callerTaskDTO, channel);
                //判断是否重试
                if (result && callerTaskDTO.getRetryNum() > 0) {
                    retryExecute(callerTaskDTO, channel);
                }
            }
        });
    }

    /**
     * 重试
     *
     * @param callerTaskDTO
     * @param channel
     */
    private void retryExecute(CallerTaskDTO callerTaskDTO, Channel channel) {
        TaskDetailInfoBO taskDetailInfoBO = getDetailInfo(callerTaskDTO);
        retryService.execute(() -> {
            for (byte retryNum = 0, totalNum = callerTaskDTO.getRetryNum(); retryNum < totalNum; retryNum++) {
                boolean result = executionSchedule(callerTaskDTO, channel);
                if (result) {
                    taskDetailInfoBO.setExecutionStatus(RETRYING.getCode());
                    taskDetailInfoBO.setRetryNum(retryNum);
                    Boolean updateResult = taskDetailInfoService.update(taskDetailInfoBO);
                    log.info("retryExecute#更新重试中状态结果：{}", updateResult);
                    taskLogService.save(taskDetailInfoBO, parse(channel), retryNum);
                    return;
                }
            }
        });
    }

    private TaskDetailInfoBO getDetailInfo(CallerTaskDTO callerTaskDTO) {
        TaskDetailInfoBO taskDetailInfoBO = taskDetailInfoService.get(callerTaskDTO.getGroupKey(),
                callerTaskDTO.getBizKey(),
                callerTaskDTO.getTopic(),
                callerTaskDTO.getExecutionTime());
        return taskDetailInfoBO;
    }


    /**
     * 执行调度
     *
     * @return
     */
    private boolean executionSchedule(CallerTaskDTO callerTaskDTO,
                                      Channel channel) {
        Value<Boolean> value = new Value<>(false);
        channel.writeAndFlush(ProtocolMsg.toEntity(callerTaskDTO)).addListener(future -> {
            try {
                log.info("processResult#执行参数:{}", callerTaskDTO);
                future.get(callerTaskDTO.getTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                value.setValue(true);
                log.error("调用方法超时或者遇到异常！参数：{}", callerTaskDTO, e);
            }
        });
        return value.getValue();
    }


    @Override
    public void stop() {
        toggle = false;
        ThreadPoolRegisterCenter.destroy(worker, scheduledExecutor, listeningWorker, executorService);
    }

}
