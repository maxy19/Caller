package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.bo.TaskLogBO;

/**
 * @Author maxy
 **/
public interface CommonService {

    default String getUniqueName(TaskDetailInfoBO taskDetailInfoBO) {
        return String.join(":", taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey(), taskDetailInfoBO.getTopic());
    }

    default String getUniqueName(String groupKey, String bizKey, String topic) {
        return String.join(":", groupKey, bizKey, topic);
    }

    default String getGroupName(TaskDetailInfoBO taskDetailInfoBO) {
        return String.join(":", taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey());
    }

    default String getUniqueName(TaskBaseInfoBO taskBaseInfoBO) {
        return String.join(":", taskBaseInfoBO.getGroupKey(), taskBaseInfoBO.getBizKey(), taskBaseInfoBO.getTopic());
    }

    default String getUniqueName(TaskLogBO taskLogBO) {
        return String.join(":", taskLogBO.getGroupKey(), taskLogBO.getBizKey(), taskLogBO.getTopic());
    }

    String ALARM_EMAIL = "alarmEmail";
    String STRATEGY_VALUE = "strategyValue";
    int ONE_MINUTE = 60;
    int ONE_HOUR = ONE_MINUTE * 60;

}
