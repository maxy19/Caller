package com.maxy.caller.model;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 客户端注册的地址
 *
 * @author maxy
 * @date   2021-01-27
 */
@Data
public class TaskRegistry {
    /**
     *  pk
     */
    private Integer id;
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
    /**
     * 更新时间
     */
    private Date createTime;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}