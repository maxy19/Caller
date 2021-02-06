package com.maxy.caller.admin.worker;

import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.service.TaskLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 查询日志并报警
 *
 * @Author maxy
 **/
@Component
@Log4j2
public class MonitorAlarmWorker implements AdminWorker {

    private ScheduledThreadPoolExecutor scheduleWorker = ThreadPoolConfig.getInstance().getPublicScheduledExecutor(true);
    @Resource
    private TaskLogService taskLogService;

    @Override
    public void start() {
        scheduleWorker.scheduleWithFixedDelay(() -> {
            try {

            } catch (Exception e) {
                log.error("监控报警定时出现异常!!", e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        ThreadPoolRegisterCenter.destroy(scheduleWorker);
    }
}
