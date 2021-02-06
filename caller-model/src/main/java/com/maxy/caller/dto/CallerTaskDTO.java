package com.maxy.caller.dto;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 服务端通知客户端执行
 * @author maxy
 */
@Data
public class CallerTaskDTO {

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

    public String getUniqueKey(){
        return StringUtils.join(groupKey,bizKey,topic,":");
    }


    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

}