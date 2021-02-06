package com.maxy.caller.core.service;

import com.maxy.caller.bo.TaskBaseInfoBO;
import com.maxy.caller.bo.TaskDetailInfoBO;

/**
 * @Author maxuyang
 **/
public interface CommonService {

    default String getUniqueName(TaskDetailInfoBO taskDetailInfoBO) {
        return String.join(":", taskDetailInfoBO.getGroupKey(), taskDetailInfoBO.getBizKey(), taskDetailInfoBO.getTopic());
    }

    default String getGroupName(TaskBaseInfoBO taskBaseInfoBO) {
        return String.join(":", taskBaseInfoBO.getGroupKey(), taskBaseInfoBO.getBizKey());
    }

    default String getUniqueName(TaskBaseInfoBO taskBaseInfoBO) {
        return String.join(":", taskBaseInfoBO.getGroupKey(), taskBaseInfoBO.getBizKey(), taskBaseInfoBO.getTopic());
    }
}
