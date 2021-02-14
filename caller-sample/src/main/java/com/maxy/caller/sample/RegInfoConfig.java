package com.maxy.caller.sample;

import com.maxy.caller.pojo.RegConfigInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Maxy
 */
@Configuration
public class RegInfoConfig {

    @Value("${caller.reg.remote.ip:10.221.36.5}")
    private String remoteIp;

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
        regConfigInfo.setBizKey("order");
        regConfigInfo.setGroupKey("taobao");
        regConfigInfo.setAccessToken(accessToken);
        regConfigInfo.setRemoteIp(remoteIp);
        regConfigInfo.setRemotePort(remotePort);
        return regConfigInfo;
    }
}