package com.maxy.caller.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 任务信息项表
 *
 * @author maxy
 * @date 2021-01-27
 */
@Data
public class TaskDetailInfoBO {

    /**
     * pk
     */
    private Long id;
    /**
     * 任务组ID
     */
    private String groupKey;

    /**
     * 业务ID
     */
    private String bizKey;

    /**
     * 任务主题
     */
    private String topic;

    /**
     * 执行参数
     */
    private String executionParam;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date executionTime;

    /**
     * 0:未上线 1:已上线 2.暂停 3:执行中 4:重试中 5:执行成功 6:执行失败
     */
    @JsonIgnore
    private Byte executionStatus;

    /**
     * 超时时间 单位毫秒
     */
    private Integer timeout;

    /**
     * 重试次数 默认 0：不执行重试
     */
    private Byte retryNum;
    /**
     * 错误信息
     */
    private String errorMsg;

    public void fillStatusAndErrorMsg(String errorMsg, Byte status) {
        this.errorMsg = errorMsg;
        this.setExecutionStatus(status);
    }

    @Override
    public String toString() {
        return "{\"TaskDetailInfoBO\":{"
                + "\"id\":"
                + id
                + ",\"groupKey\":\""
                + groupKey + '\"'
                + ",\"bizKey\":\""
                + bizKey + '\"'
                + ",\"topic\":\""
                + topic + '\"'
                + ",\"executionParam\":\""
                + executionParam + '\"'
                + ",\"executionTime\":\""
                + executionTime + '\"'
                + ",\"executionStatus\":"
                + executionStatus
                + ",\"timeout\":"
                + timeout
                + ",\"retryNum\":"
                + retryNum
                + ",\"errorMsg\":\""
                + errorMsg + '\"'
                + "}}";

    }
}