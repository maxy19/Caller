package com.maxy.caller.model;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

import java.util.Date;

/**
 * 任务组
 *
 * @author maxy
 * @date   2021-01-25
 */
@Data
public class TaskGroup {
    /**
     * PK
     */
    private Long id;

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