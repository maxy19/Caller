package com.maxy.caller.core.mpmc;

import com.lmax.disruptor.WorkHandler;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * @author maxuyang
 */
@Log4j2
public class Consumer implements WorkHandler<Event<List<DelayTask>>> {



    @Override
    public void onEvent(Event<List<DelayTask>> listEvent) throws Exception {
        List<TaskDetailInfoBO> taskDetailInfoBOList = BeanCopyUtils.copyListProperties(listEvent.getElement(), TaskDetailInfoBO::new);
        //taskDetailInfoService.batchInsert(taskDetailInfoBOList);
        //taskLogService.batchInsert(taskDetailInfoBOList, ONLINE.getCode(), parse(channel));
    }

}