package com.maxy.caller.admin.service.Impl;

import com.maxy.caller.admin.enums.AlarmStatusEnum;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.bo.TaskLogBO;
import com.maxy.caller.common.utils.BeanCopyUtils;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.service.TaskLogService;
import com.maxy.caller.model.TaskLog;
import com.maxy.caller.persistent.example.TaskLogExample;
import com.maxy.caller.persistent.mapper.TaskLogExtendMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.maxy.caller.core.enums.ExecutionStatusEnum.EXECUTION_FAILED;

/**
 * @Author maxy
 **/
@Service
public class TaskLogServiceImpl implements TaskLogService {

    @Resource
    private TaskLogExtendMapper taskLogExtendMapper;

    @Override
    public void deleteBySpecifiedTime(Date specifiedTime) {
        TaskLogExample example = new TaskLogExample();
        example.createCriteria().andCreateTimeLessThan(specifiedTime);
        taskLogExtendMapper.deleteByExample(example);
    }

    @Override
    public List<TaskLogBO> findFailLog() {
        TaskLogExample example = new TaskLogExample();
        example.createCriteria().andExecutorStatusEqualTo(EXECUTION_FAILED.getCode())
                .andAlarmStatusEqualTo(AlarmStatusEnum.DEFAULT.getCode())
                .andExecutorResultMsgIsNotNull();
        List<TaskLog> taskLogs = taskLogExtendMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskLogs)) {
            return Collections.emptyList();
        }
        return BeanCopyUtils.copyListProperties(taskLogs, TaskLogBO::new);
    }

    @Override
    public boolean batchInsert(List<TaskDetailInfoBO> taskDetailInfoBOList, Byte executorStatus, String address) {
        List<TaskLog> collect = taskDetailInfoBOList.stream().map(taskDetailInfoBO -> {
            TaskLog taskLog = new TaskLog();
            taskLog.setGroupKey(taskDetailInfoBO.getGroupKey());
            taskLog.setBizKey(taskDetailInfoBO.getBizKey());
            taskLog.setTopic(taskDetailInfoBO.getTopic());
            taskLog.setExecuteParam(taskDetailInfoBO.getExecutionParam());
            taskLog.setExecutorTime(taskDetailInfoBO.getExecutionTime());
            taskLog.setRetryCount((byte) 0);
            taskLog.setExecutorAddress(address);
            taskLog.setExecutorStatus(executorStatus);
            taskLog.setAlarmStatus(AlarmStatusEnum.DEFAULT.getCode());
            return taskLog;
        }).collect(Collectors.toList());
        return taskLogExtendMapper.batchInsert(collect) > 0;
    }

    @Override
    public boolean batchInsert(List<TaskDetailInfoBO> taskDetailInfoBOList, Byte executorStatus) {
        return batchInsert(taskDetailInfoBOList,executorStatus,null);
    }

    @Override
    public boolean save(TaskDetailInfoBO taskDetailInfoBO, String executeAddress, Byte retryNum) {
        TaskLog taskLog = new TaskLog();
        if (StringUtils.isNotBlank(executeAddress)) {
            taskLog.setExecutorAddress(executeAddress);
        }
        if (Objects.nonNull(retryNum)) {
            taskLog.setRetryCount(retryNum);
        }
        taskLog.setGroupKey(taskDetailInfoBO.getGroupKey());
        taskLog.setBizKey(taskDetailInfoBO.getBizKey());
        taskLog.setTopic(taskDetailInfoBO.getTopic());
        taskLog.setExecuteParam(taskDetailInfoBO.getExecutionParam());
        taskLog.setExecutorTime(taskDetailInfoBO.getExecutionTime());
        taskLog.setExecutorStatus(taskDetailInfoBO.getExecutionStatus());
        taskLog.setAlarmStatus(AlarmStatusEnum.DEFAULT.getCode());
        taskLog.setCreateTime(DateUtils.getNowTime());
        taskLog.setExecutorResultMsg(taskDetailInfoBO.getErrorMsg());
        return taskLogExtendMapper.insertSelective(taskLog) > 0;
    }

    @Override
    public boolean save(TaskDetailInfoBO taskDetailInfoBO) {
        return save(taskDetailInfoBO, null, null);
    }

    @Override
    public boolean saveClientResult(TaskDetailInfoBO taskDetailInfoBO, String message, String executeAddress) {
        TaskLog taskLog = new TaskLog();
        taskLog.setGroupKey(taskDetailInfoBO.getGroupKey());
        taskLog.setBizKey(taskDetailInfoBO.getBizKey());
        taskLog.setTopic(taskDetailInfoBO.getTopic());
        taskLog.setExecuteParam(taskDetailInfoBO.getExecutionParam());
        taskLog.setExecutorTime(taskDetailInfoBO.getExecutionTime());
        taskLog.setRetryCount((byte) 0);
        taskLog.setExecutorStatus(taskDetailInfoBO.getExecutionStatus());
        taskLog.setExecutorAddress(executeAddress);
        if (StringUtils.isNotBlank(message)) {
            taskLog.setExecutorResultMsg(message);
        }
        taskLog.setCreateTime(DateUtils.getNowTime());
        taskLog.setAlarmStatus(AlarmStatusEnum.DEFAULT.getCode());
        taskLog.setExecutorResultMsg(taskDetailInfoBO.getErrorMsg());
        return taskLogExtendMapper.insert(taskLog) > 0;
    }

    @Override
    public boolean updateAlarmStatus(TaskLogBO taskLogBO, Byte sourceStatus, Byte targetStatus) {
        TaskLog taskLog = new TaskLog();
        BeanUtils.copyProperties(taskLogBO, taskLog);
        TaskLogExample example = new TaskLogExample();
        example.createCriteria().andExecutorTimeEqualTo(taskLog.getExecutorTime())
                .andAlarmStatusEqualTo(sourceStatus)
                .andGroupKeyEqualTo(taskLog.getGroupKey())
                .andBizKeyEqualTo(taskLog.getBizKey())
                .andTopicEqualTo(taskLog.getTopic());
        taskLog.setAlarmStatus(targetStatus);
        taskLog.setCreateTime(DateUtils.getNowTime());
        return taskLogExtendMapper.updateByExample(taskLog, example) > 0;
    }


}
