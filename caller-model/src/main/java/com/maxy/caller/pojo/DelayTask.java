package com.maxy.caller.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author maxy
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelayTask {
    /**
     * 组名称关键字
     */
    private String groupKey;
    /**
     * 业务名称关键字
     */
    private String bizKey;
    /**
     * 具体任务名称
     */
    private String topic;
    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date executionTime;
    /**
     * 执行参数
     */
    private String executionParam;
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

