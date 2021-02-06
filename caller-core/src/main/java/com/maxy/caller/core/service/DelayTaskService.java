package com.maxy.caller.core.service;

import com.maxy.caller.pojo.DelayTask;

import java.util.List;

/**
 * @Author maxuyang
 **/
public interface DelayTaskService {

    boolean send(List<DelayTask> delayTasks);
}
