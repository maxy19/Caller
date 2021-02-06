package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskDetailInfoBO;

import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskLogService extends CommonService {

    void deleteBySpecifiedTime(Date specifiedTime);

    boolean batchInsert(List<TaskDetailInfoBO> preReadInfoList);

    boolean save(TaskDetailInfoBO taskDetailInfoBO);

}
