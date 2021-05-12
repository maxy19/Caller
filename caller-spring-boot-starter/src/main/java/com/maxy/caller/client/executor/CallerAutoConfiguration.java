package com.maxy.caller.client.executor;

import com.maxy.caller.common.utils.IpUtils;
import com.maxy.caller.pojo.RegConfigInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author maxuyang
 */
@ConfigurationProperties(prefix = "caller.reg")
@Configuration
@EnableConfigurationProperties
public class CallerAutoConfiguration {

    //@Value("${caller.reg.remote.addresses:127.0.0.1}")
    private String remoteIp = IpUtils.getIp();

    @Value("${caller.reg.remote.port:8888}")
    private Integer remotePort;

    @Value("${caller.reg.accessToken:null}")
    private String accessToken;

    @Value("${caller.reg.groupKey:null}")
    private String groupKey;

    @Value("${caller.reg.bizKey:null}")
    private String bizKey;

    @Bean
    public RegConfigInfo regConfigInfo() {
        RegConfigInfo regConfigInfo = new RegConfigInfo();
        regConfigInfo.setBizKey(bizKey);
        regConfigInfo.setGroupKey(groupKey);
        regConfigInfo.setRemoteAddresses(StringUtils.joinWith(":", remoteIp, remotePort));
        return regConfigInfo;
    }

}
