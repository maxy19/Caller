package com.maxy.caller.core.service;

import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.pojo.RetryConfig;

import java.util.List;

/**
 * @Author maxy
 **/
public interface DelayTaskService {

    boolean send(List<DelayTask> delayTasks);

    boolean send(List<DelayTask> delayTasks, RetryConfig retryConfig) throws InterruptedException;
}
