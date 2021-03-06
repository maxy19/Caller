package com.maxy.caller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxy.caller.bo.TaskDetailInfoBO;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 服务端通知客户端执行
 *
 * @author maxy
 */
@Data
public class CallerTaskDTO {
    /**
     * 详情id
     */
    private Long detailTaskId;
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
     * 超时时间 单位毫秒
     */
    private Integer timeout;

    /**
     * 重试次数 默认 0：不执行重试
     */
    private Byte retryNum;

    @JsonIgnore
    public String getUniqueKey() {
        return String.join(":", groupKey, bizKey, topic);
    }
    @JsonIgnore
    public CallerTaskDTO fullField(TaskDetailInfoBO bo) {
        setGroupKey(bo.getGroupKey());
        setTopic(bo.getTopic());
        setExecutionParam(bo.getExecutionParam());
        setExecutionTime(bo.getExecutionTime());
        setTimeout(bo.getTimeout());
        setRetryNum(bo.getRetryNum());
        setDetailTaskId(bo.getId());
        setBizKey(bo.getBizKey());
        return this;
    }


    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

}