package com.maxy.caller.model;

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
public class TaskDetailInfo {
    /**
     * PK
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}