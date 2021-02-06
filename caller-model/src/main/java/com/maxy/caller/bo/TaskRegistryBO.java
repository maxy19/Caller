package com.maxy.caller.bo;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

/**
 * @Author maxy
 **/
@Data
public class TaskRegistryBO {

    /**
     * 组key
     */
    private String groupKey;

    /**
     * 业务key
     */
    private String bizKey;

    /**
     * 客户端地址
     */
    private String registryAddress;


    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
