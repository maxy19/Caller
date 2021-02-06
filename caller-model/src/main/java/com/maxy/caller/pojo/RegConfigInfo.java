package com.maxy.caller.pojo;

import com.maxy.caller.common.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端配置信息
 *
 * @author maxy
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegConfigInfo {

    protected String remoteIp;
    protected Integer remotePort;
    protected String accessToken;
    protected String groupKey;
    protected String bizKey;

    public String getUniqName() {
        return String.join(":", groupKey, bizKey);
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }


}
