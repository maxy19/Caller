package com.maxy.caller.admin.service.Impl;

import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.model.TaskLog;
import com.maxy.caller.persistent.example.TaskLogExample;
import com.maxy.caller.persistent.mapper.TaskLogExtendMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author maxy
 **/
@Service
public class TaskLogServiceImpl implements TaskLogService {

    @Resource
    private TaskLogExtendMapper taskLogExtendMapper;
    @Resource
    private TaskBaseInfoService taskBaseInfoService;

    @Override
    public void deleteBySpecifiedTime(Date specifiedTime) {
        TaskLogExample example = new TaskLogExample();
        example.createCriteria().andCreateTimeLessThan(specifiedTime);
        taskLogExtendMapper.deleteByExample(example);
    }

    @Override
    public boolean batchInsert(List<TaskDetailInfoBO> taskDetailInfoBOList) {
        List<TaskLog> collect = taskDetailInfoBOList.stream().map(taskDetailInfoBO -> {
            TaskLog taskLog = new TaskLog();
            taskLog.setGroupKey(taskDetailInfoBO.getGroupKey());
            taskLog.setBizKey(taskDetailInfoBO.getBizKey());
            taskLog.setTopic(taskDetailInfoBO.getTopic());
            taskLog.setExecuteParam(taskDetailInfoBO.getExecutionParam());
            taskLog.setExecutorTime(taskDetailInfoBO.getExecutionTime());
            taskLog.setRetryCount(taskDetailInfoBO.getRetryNum());
            taskLog.setExecutorStatus(taskDetailInfoBO.getExecutionStatus());
            taskLog.setAlarmStatus((byte) 0);
            return taskLog;
        }).collect(Collectors.toList());
        return taskLogExtendMapper.batchInsert(collect) > 0;
    }

    @Override
    public boolean save(TaskDetailInfoBO taskDetailInfoBO) {
        TaskLog taskLog = new TaskLog();
        taskLog.setGroupKey(taskDetailInfoBO.getGroupKey());
        taskLog.setBizKey(taskDetailInfoBO.getBizKey());
        taskLog.setTopic(taskDetailInfoBO.getTopic());
        taskLog.setExecuteParam(taskDetailInfoBO.getExecutionParam());
        taskLog.setExecutorTime(taskDetailInfoBO.getExecutionTime());
        taskLog.setRetryCount(taskDetailInfoBO.getRetryNum());
        taskLog.setExecutorStatus(taskDetailInfoBO.getExecutionStatus());
        return taskLogExtendMapper.insert(taskLog) > 0;
    }
}
