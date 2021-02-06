package com.maxy.caller.admin.service.Impl;

import com.maxy.caller.core.service.TaskLockService;
import com.maxy.caller.persistent.mapper.TaskLockMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author maxy
 **/
@Service
public class TaskLockServiceImpl implements TaskLockService {
    @Resource
    private TaskLockMapper taskLockMapper;

    @Override
    public void lockForUpdate() {
        taskLockMapper.lockForUpdate();
    }
}
