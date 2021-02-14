package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskDetailInfoBO;

import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskLogService extends CommonService {

    void deleteBySpecifiedTime(Date specifiedTime);

    boolean initByBatchInsert(List<TaskDetailInfoBO> preReadInfoList);

    boolean save(TaskDetailInfoBO taskDetailInfoBO, String executeAddress, Byte retryNum);

    boolean saveClientResult(TaskDetailInfoBO taskDetailInfoBO, String executeAddres);

}
