package com.maxy.caller.remoting.server.netty.mpmc;

import com.lmax.disruptor.WorkHandler;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.core.service.TaskDetailInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.ONLINE;

/**
 * @author maxuyang
 */
@Log4j2
public class Consumer implements WorkHandler<Event<List<DelayTask>>> {

    private TaskDetailInfoService taskDetailInfoService;
    private TaskLogService taskLogService;

    public Consumer(TaskDetailInfoService taskDetailInfoService, TaskLogService taskLogService) {
        this.taskDetailInfoService = taskDetailInfoService;
        this.taskLogService = taskLogService;
    }

    @Override
    public void onEvent(Event<List<DelayTask>> event) throws Exception {
        List<TaskDetailInfoBO> taskDetailInfoBOList = BeanCopyUtils.copyListProperties(event.getElement(), TaskDetailInfoBO::new);
        taskDetailInfoService.batchInsert(taskDetailInfoBOList);
        taskLogService.batchInsert(taskDetailInfoBOList, ONLINE.getCode(), event.getAddress());
    }

}