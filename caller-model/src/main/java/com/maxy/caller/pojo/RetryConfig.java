package com.maxy.caller.pojo;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @Author maxuyang
 **/
@Data
public class RetryConfig {

    private Integer retryNum = 1;

    private Integer delayTime = 1;

    private TimeUnit delayTimeUnit = TimeUnit.SECONDS;



}
