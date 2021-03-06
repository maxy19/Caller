package com.maxy.caller.remoting.server.netty.spmc;

import com.google.common.base.Stopwatch;
import com.lmax.disruptor.WorkHandler;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.GeneralConfigCenter;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.service.Cache;
import com.maxy.caller.core.service.CommonService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.core.utils.CallerUtils;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maxy.caller.core.constant.ThreadConstant.HANDLE_CONSUMER_THREAD_POOL;
import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;
import static com.maxy.caller.core.enums.GenerateKeyEnum.REPEAT_TASK_INFO;
import static com.maxy.caller.core.enums.GenerateKeyEnum.ZSET_QUEUE_FORMAT;

/**
 * @author maxy
 */
@Log4j2
public class Consumer implements CommonService, WorkHandler<Event<List<DelayTask>>> {

    private TaskDetailInfoService taskDetailInfoService;
    private TaskLogService taskLogService;
    private Cache cache;
    private GeneralConfigCenter generalConfigCenter;
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig.getInstance();
    private ExecutorService consumerExecutor = threadPoolConfig.getPublicThreadPoolExecutor(true, HANDLE_CONSUMER_THREAD_POOL);
    public Consumer(TaskDetailInfoService taskDetailInfoService,
                    TaskLogService taskLogService,
                    Cache cache,
                    GeneralConfigCenter generalConfigCenter) {
        this.taskDetailInfoService = taskDetailInfoService;
        this.taskLogService = taskLogService;
        this.cache = cache;
        this.generalConfigCenter = generalConfigCenter;
    }

    @Override
    public void onEvent(Event<List<DelayTask>> event) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        consumerExecutor.execute(() -> {
            //基础校验
            List<TaskDetailInfoBO> taskDetailInfoBOList = validate(event);
            //批量插入
            List<TaskDetailInfoBO> newTaskDetailBOList = taskDetailInfoService.batchInsert(taskDetailInfoBOList);
            //插入失败发报警
            if (CollectionUtils.isEmpty(newTaskDetailBOList)) {
                //todo 发送失败邮件
                log.error("onEvent#插入数据失败!!!!");
                return;
            }
            //添加redis
            addZSetQueue(newTaskDetailBOList, generalConfigCenter.getTotalSlot());
            //记录log
            taskLogService.batchInsert(newTaskDetailBOList, ONLINE.getCode(), CallerUtils.parse(event.getChannel()));
        });
        log.info("onEvent#耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private List<TaskDetailInfoBO> validate(Event<List<DelayTask>> event) {
        //校验是否存储
        if (CollectionUtils.isEmpty(event.getElement())) {
            log.warn("validate#没有找到延迟任务,校验失败.");
            return Collections.emptyList();
        }
        List<TaskDetailInfoBO> taskDetailInfoBOList = BeanCopyUtils.copyListProperties(event.getElement(), TaskDetailInfoBO::new);
        //返回未添加的任务
        return taskDetailInfoBOList.stream().filter(ele -> {
            //同一个时间出现同样的参数就认定为重复数据并做幂等过滤
            String param = cache.get(REPEAT_TASK_INFO.join(getGroupName(ele), ele.getExecutionTime()));
            if(Objects.equals(param, StringUtils.defaultIfBlank(ele.getExecutionParam(), NULL_STR))){
                log.warn("validate#校验发现有重复数据:{}.", ele);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 添加zset队列
     *
     * @param preReadInfoList
     * @param size
     */
    private void addZSetQueue(List<TaskDetailInfoBO> preReadInfoList, int size) {
        for (TaskDetailInfoBO taskDetailInfoBO : preReadInfoList) {
            long invokeTime = taskDetailInfoBO.getExecutionTime().getTime();
            //消除bigKey则将队列通过{node-1}分片 打散映射
            long slot = mod(invokeTime, size);
            if (cache.zadd(ZSET_QUEUE_FORMAT.join(slot), (double) invokeTime, JSONUtils.toJSONString(taskDetailInfoBO)) < 1) {
                //写入redis队列失败,更改状态为online存储,后台使用扫表重试.
                taskDetailInfoBO.setExecutionStatus(ONLINE.getCode());
                taskDetailInfoService.update(taskDetailInfoBO);
                continue;
            }
            //处理幂等任务
            handleRepeatingData(taskDetailInfoBO, invokeTime);
        }
    }

    /**
     * 增加缓存幂等
     *
     * @param detailBo
     * @param invokeTime
     */
    private String handleRepeatingData(TaskDetailInfoBO detailBo, long invokeTime) {
        long diffTime = invokeTime - System.currentTimeMillis();
        if(diffTime < 1){
           diffTime = ONE_MINUTE;
        }
        long remainingTime = TimeUnit.MILLISECONDS.toSeconds(diffTime) + TimeUnit.MILLISECONDS.toMillis(RandomUtils.nextInt(1, 100));
        //单个detail信息
        String key = REPEAT_TASK_INFO.join(getGroupName(detailBo), invokeTime);
        return cache.setnx(key, (int) remainingTime, StringUtils.defaultIfBlank(detailBo.getExecutionParam(), NULL_STR));
    }

}