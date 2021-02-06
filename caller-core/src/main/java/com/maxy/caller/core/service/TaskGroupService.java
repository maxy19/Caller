package com.maxy.caller.core.service;

import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskGroupBO;

import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskGroupService {

    PageInfo<TaskGroupBO> list(QueryConditionBO queryConditionBO);

    Boolean save(TaskGroupBO taskGroupBO);

    Boolean update(TaskGroupBO taskGroupBO);

    List<TaskGroupBO> getAllList();

    List<TaskGroupBO> getListByLogRetentionDay(Short days);

    List<TaskGroupBO> getAllListByAddressType(Byte addressType);

    Boolean deleteByGroupKeyAndBizKey(String groupKey, String bizKey);

    TaskGroupBO getByGroupKeyAndBizKey(String groupKey, String bizKey);

    TaskGroupBO updateStatus(TaskGroupBO taskGroupBO, Byte sourceStatus, Byte targetStatus);
}
