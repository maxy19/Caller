package com.maxy.caller.pojo;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.maxy.caller.common.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    protected String remoteAddresses;
    protected String accessToken;
    protected String groupKey;
    protected String bizKey;

    public String getUniqName() {
        return String.join(":", groupKey, bizKey);
    }

    @Data
    @AllArgsConstructor
    public static class AddressInfo {
        private String ip;
        private Integer port;
    }

    public List<AddressInfo> getAddressInfos() {
        List<String> remoteAddress = Splitter.on(",").splitToList(remoteAddresses);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(remoteAddress), "请填写远程服务端地址,至少有一个.");
        return remoteAddress.stream().map(address -> {
            List<String> ipAndPort = Splitter.on(":").splitToList(address);
            Preconditions.checkArgument(ipAndPort.size() == 2,"缺少地址信息!!");
            return new AddressInfo(ipAndPort.get(0), Integer.parseInt(ipAndPort.get(1)));
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }


}
