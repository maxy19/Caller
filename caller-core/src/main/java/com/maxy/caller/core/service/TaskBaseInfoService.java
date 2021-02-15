package com.maxy.caller.core.service;

import com.github.pagehelper.PageInfo;
import com.maxy.caller.bo.QueryConditionBO;
import com.maxy.caller.bo.TaskBaseInfoBO;

import java.util.Date;
import java.util.List;

/**
 * @Author maxy
 **/
public interface TaskBaseInfoService extends CommonService {


    PageInfo<TaskBaseInfoBO> list(QueryConditionBO queryConditionBO);

    Boolean save(TaskBaseInfoBO taskBaseInfoBO);

    Boolean update(TaskBaseInfoBO taskInfoBO);

    Boolean delete(Long taskInfoId);

    TaskBaseInfoBO getById(Long taskBaseInfoId);

    TaskBaseInfoBO getByUniqueKey(String groupKey, String bizKey, String topic);

    List<TaskBaseInfoBO> getTaskInfoBOList(Date currentTime, Byte executionStatus, int addSecond);

    Byte getRouterStrategy(String groupKey, String bizKey, String topic);

    String getAlarmEmail(String groupKey, String bizKey, String topic);
}
