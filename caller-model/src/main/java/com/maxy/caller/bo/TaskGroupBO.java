package com.maxy.caller.bo;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

/**
 * @author maxy
 */
@Data
public class TaskGroupBO {

    /**
     * 组名称关键字
     */
    private String groupKey;

    /**
     * 业务名称关键字
     */
    private String bizKey;

    /**
     * 上传地址类型 1:自动上传 2:手动上传
     */
    private Byte addressType;

    /**
     * 日志保留天数
     */
    private Short logRetentionDays;

    /**
     * 地址列表
     */
    private String addressList;

    /**
     *  0 未加锁 1 加锁
     */
    private Byte status;



    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}