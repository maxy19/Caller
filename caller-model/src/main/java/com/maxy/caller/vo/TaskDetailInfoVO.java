package com.maxy.caller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 任务信息项表
 *
 * @author maxy
 * @date   2021-01-27
 */
@Data
public class TaskDetailInfoVO {

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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date executionTime;

    /**
     * 0:未上线 1:已上线 2.暂停 3:执行中 4:重试中 5:执行成功 6:执行失败
     */
    private Byte executionStatus;

    /**
     * 超时时间 单位毫秒
     */
    private Integer timeout;

    /**
     * 重试次数 默认 0：不执行重试
     */
    private Byte retryNum;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}