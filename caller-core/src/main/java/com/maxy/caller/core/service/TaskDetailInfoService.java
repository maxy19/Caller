package com.maxy.caller.core.service;

import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.dto.CallerTaskDTO;

import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskDetailInfoService extends CommonService {

    PageInfo<TaskDetailInfoBO> list(QueryConditionBO queryConditionBO);

    Boolean save(TaskDetailInfoBO taskDetailInfoBO);

    Boolean update(TaskDetailInfoBO taskDetailInfoBO);

    Boolean delete(Long taskInfoId);

    TaskDetailInfoBO getByInfoId(Long taskInfoId);

    List<TaskDetailInfoBO> getPreReadInfo(Byte status, Date endTime);

    boolean batchInsert(List<TaskDetailInfoBO> taskDetailInfoBOList);

    boolean updateStatusByIds(List<Long> ids, Byte sourceStatus, Byte targetStatus);

    List<TaskDetailInfoBO> getTaskDetailList(String groupKey, String bizKey, String topic);

    TaskDetailInfoBO get(String groupKey, String bizKey, String topic, Date executionTime);

    void removeBackup(CallerTaskDTO callerTaskDTO);
}
