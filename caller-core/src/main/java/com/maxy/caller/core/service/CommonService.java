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

    default long mod(long time, int masterSize) {
        return time & masterSize;
    }

    int PROCESSORS = Runtime.getRuntime().availableProcessors();
    int CORE_SIZE = PROCESSORS * 2;
    int MAX_SIZE = CORE_SIZE + CORE_SIZE / 2;

    String ALARM_EMAIL = "alarmEmail";
    String STRATEGY_VALUE = "strategyValue";
    String NULL_STR = "NULL";
    int ONE_MINUTE = 60;
    int ONE_MILLISECOND = 1;
    int ONE_SECOND = ONE_MILLISECOND * 1000;
    int ONE_HOUR = ONE_MINUTE * 60;
    int ONE_DAY = ONE_HOUR * 24;
    int THREE_DAY = 3 * ONE_HOUR * 24;
    int SERVER_DAY = 7 * ONE_HOUR * 24;
    int TEN_MINUTE_OF_SECOND = 10 * 60 * 1000;
}
