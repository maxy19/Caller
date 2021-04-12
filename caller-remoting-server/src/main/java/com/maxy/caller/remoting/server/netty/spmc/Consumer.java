package com.maxy.caller.remoting.server.netty.spmc;

import com.lmax.disruptor.WorkHandler;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.core.config.GeneralConfigCenter;
import com.maxy.caller.core.service.Cache;
import com.maxy.caller.core.service.CommonService;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;
import static com.maxy.caller.core.enums.GenerateKeyEnum.DETAIL_TASK_INFO;
import static com.maxy.caller.core.enums.GenerateKeyEnum.ZSET_QUEUE_FORMAT;

/**
 * @author maxuyang
 */
@Log4j2
public class Consumer implements CommonService, WorkHandler<Event<List<DelayTask>>> {

    private TaskDetailInfoService taskDetailInfoService;
    private TaskLogService taskLogService;
    private Cache cache;
    private GeneralConfigCenter generalConfigCenter;

    public Consumer(TaskDetailInfoService taskDetailInfoService, TaskLogService taskLogService, Cache cache, GeneralConfigCenter generalConfigCenter) {
        this.taskDetailInfoService = taskDetailInfoService;
        this.taskLogService = taskLogService;
        this.cache = cache;
        this.generalConfigCenter = generalConfigCenter;
    }

    @Override
    public void onEvent(Event<List<DelayTask>> event) throws Exception {
        List<TaskDetailInfoBO> newTaskDetailBOList = taskDetailInfoService.batchInsert(BeanCopyUtils.copyListProperties(event.getElement(), TaskDetailInfoBO::new));
        if (newTaskDetailBOList.size() > 0) {
            addZSetQueue(newTaskDetailBOList, generalConfigCenter.getTotalSlot());
        } else {
            //todo 发送报警邮件
            log.error("onEvent#插入数据失败!!!!");
            return;
        }
        taskLogService.batchInsert(newTaskDetailBOList, ONLINE.getCode(), event.getAddress());
    }

    /**
     * 添加zset队列
     *
     * @param preReadInfoList
     * @param size
     */
    private void addZSetQueue(List<TaskDetailInfoBO> preReadInfoList, int size) {
        for (TaskDetailInfoBO taskDetailInfoBO : preReadInfoList) {
            long time = taskDetailInfoBO.getExecutionTime().getTime();
            //消除bigKey则将队列通过{node-1}分片 打散映射
            long slot = mod(time, size);
            Long result = cache.zadd(ZSET_QUEUE_FORMAT.join(slot), (double) time, JSONUtils.toJSONString(taskDetailInfoBO));
            if (result < 1) {
                taskDetailInfoBO.setExecutionStatus(ONLINE.getCode());
                taskDetailInfoService.update(taskDetailInfoBO);
                continue;
            }
            cacheDetailTaskInfo(taskDetailInfoBO, time);
        }
    }

    /**
     * 缓存详情任务信息
     * 过期时间 = 距离执行时间+执行时间(10000-60000毫秒随机值)避免相同时间同时过期
     *
     * @param taskDetailInfoBO
     * @param time
     */
    private void cacheDetailTaskInfo(TaskDetailInfoBO taskDetailInfoBO, long time) {
        long remainingTime = TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis()) + TimeUnit.MILLISECONDS.toMillis(RandomUtils.nextInt(10000, 60000));
        //单个detail信息
        if (remainingTime - ONE_SECOND > 0) {
            cache.set(DETAIL_TASK_INFO.join(taskDetailInfoBO.getId()), (int) remainingTime, JSONUtils.toJSONString(taskDetailInfoBO));
        }
    }

}