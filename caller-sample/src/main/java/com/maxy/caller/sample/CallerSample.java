package com.maxy.caller.sample;

import com.maxy.caller.client.executor.DelayHandler;
import com.maxy.caller.client.executor.annotation.Registered;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.dto.ResultDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author maxy
 **/
@Service
public class CallerSample implements DelayHandler {

    @Resource
    private DelayTaskService delayTaskService;

    /**
     * 到时要执行的方法
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    @Registered(topic = "clsExpireOrder")
    public ResultDTO execute(String param) throws Exception {
        System.out.println("开始执行：参数" + param);
        return ResultDTO.getSuccessResponse();
    }

}
