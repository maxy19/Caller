package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.bo.TaskLogBO;

import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskLogService extends CommonService {

    void deleteBySpecifiedTime(Date specifiedTime);

    List<TaskLogBO> findFailLog();

    boolean initByBatchInsert(List<TaskDetailInfoBO> preReadInfoList);

    boolean save(TaskDetailInfoBO taskDetailInfoBO, String executeAddress, Byte retryNum);

    boolean save(TaskDetailInfoBO taskDetailInfoBO);

    boolean saveClientResult(TaskDetailInfoBO taskDetailInfoBO, String message, String executeAddres);

    boolean updateAlarmStatus(TaskLogBO taskLogBO, Byte sourceStatus, Byte targetStatus);
}
