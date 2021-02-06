package com.maxy.caller.persistent.mapper;

import com.maxy.caller.model.TaskLog;

import java.util.List;

public interface TaskLogExtendMapper extends TaskLogMapper {

    int batchInsert(List<TaskLog> taskLogList);
}