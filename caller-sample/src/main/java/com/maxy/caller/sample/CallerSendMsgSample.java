package com.maxy.caller.sample;

import com.maxy.caller.client.executor.DelayHandler;
import com.maxy.caller.client.executor.annotation.Registered;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.dto.ResultDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author maxy
 **/
@Service
@Log4j2
public class CallerSendMsgSample implements DelayHandler {

    /**
     * 到时要执行的方法
     *
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    @Registered(topic = "sendMsg")
    public ResultDTO execute(String param) throws Exception {
        log.info("sendMsg：参数：{},时间：{}", param, DateUtils.formatDateTime(new Date()));
        Thread.sleep(5000);
        return ResultDTO.getSuccessResponse();
    }

}
