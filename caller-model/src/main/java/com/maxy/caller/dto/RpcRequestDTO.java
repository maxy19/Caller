package com.maxy.caller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maxy.caller.common.utils.JSONUtils;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.pojo.RegConfigInfo;
import lombok.Data;

import java.util.List;

/**
 * 客户端执行结果返回
 * @Author maxuyang
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RpcRequestDTO {

    private List<DelayTask> delayTasks;

    private ResultDTO resultDTO;

    private CallerTaskDTO callerTaskDTO;

    private RegConfigInfo regConfigInfo;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
