package com.maxy.caller.sample;

import com.google.common.collect.Lists;
import com.maxy.caller.common.utils.LocalDateUtils;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.pojo.DelayTask;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Author maxuyang
 **/
@Log4j2
@RestController
@RequestMapping("/sample")
public class SampleController {
    @Resource
    private DelayTaskService delayTaskService;

    @SneakyThrows
    @GetMapping("/req")
    public String req() {
        DelayTask delayTask = new DelayTask();
        delayTask.setGroupKey("taobao");
        delayTask.setBizKey("order");
        delayTask.setTopic("clsExpireOrder");
        delayTask.setExecutionTime(LocalDateUtils.plus(LocalDateTime.now(), 1, ChronoUnit.HOURS));
        delayTask.setExecutionParam("触发成功!!");
        delayTask.setTimeout(2000);
        delayTask.setRetryNum((byte) 1);
        boolean send = delayTaskService.send(Lists.newArrayList(delayTask));
        return send+"_"+System.currentTimeMillis();
    }
}
