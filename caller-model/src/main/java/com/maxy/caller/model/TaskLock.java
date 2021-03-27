package com.maxy.caller.model;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.Data;

/**
 * @author maxy
 */
@Data
public class TaskLock {
    /**
     * 名称
     */
    private String lockName;

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

}