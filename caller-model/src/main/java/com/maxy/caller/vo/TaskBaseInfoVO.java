package com.maxy.caller.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

/**
 * @author maxy
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskBaseInfoVO {


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

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

}