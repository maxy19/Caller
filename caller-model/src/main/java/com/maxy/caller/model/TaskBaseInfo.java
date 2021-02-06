package com.maxy.caller.model;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 任务信息表
 *
 * @author maxy
 * @date   2021-01-27
 */
@Data
public class TaskBaseInfo {
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
     * 任务描述
     */
    private String description;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 路由策略 1:轮询 2:随机 3:hash 4:第一台 5:最后一台
     */
    private Byte executorRouterStrategy;

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