package com.maxy.caller.model;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 任务日志记录表
 *
 * @author maxy
 * @date   2021-01-13
 */
@Data
public class TaskLog {
    /**
     * PK
     */
    private Long id;

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
     * 0:未上线 1:已上线 2.暂停 3:执行中 4:重试中 5:执行成功 6:执行失败
     */
    private Byte executorStatus;

    /**
     * 执行结果描述（json结构）
     */
    private String executorResultMsg;

    /**
     * 告警状态：0默认 -1=锁定状态 1-无需告警、2-告警成功、3-告警失败
     */
    private Byte alarmStatus;

    /**
     * 创建时间
     */
    private Date createTime;


    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}