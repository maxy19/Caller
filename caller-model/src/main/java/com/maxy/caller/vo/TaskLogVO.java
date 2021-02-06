package com.maxy.caller.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author maxy
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskLogVO {


    /**
     * 组key
     */
    private String groupKey;

    /**
     * 业务key
     */
    private String bizKey;

    /**
     * 任务主题
     */
    private String topic;

    /**
     * 执行器任务参数
     */
    private String executeParam;

    /**
     * 执行时间
     */
    private Date executorTime;

    /**
     * 执行器地址，本次执行的地址
     */
    private String executorAddress;

    /**
     * 失败重试次数
     */
    private Byte retryCount;

    /**
     * 状态 1:未开始 2:执行中 3.重试中 4:执行成功 5:执行失败
     */
    private Byte executorStatus;

    /**
     * 执行结果描述（json结构）
     */
    private String executorResultMsg;

    /**
     * 告警状态：0默认 -1锁定状态 1-无需告警、2-告警成功、3-告警失败
     */
    private Byte alarmStatus;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

}