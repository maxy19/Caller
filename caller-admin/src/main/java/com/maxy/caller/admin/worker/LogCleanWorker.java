package com.maxy.caller.admin.worker;

import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskGroupBO;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.service.TaskGroupService;
import com.maxy.caller.core.service.TaskLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 清理过期日志log表
 *
 * @Author maxy
 **/
@Component
@Log4j2
public class LogCleanWorker implements AdminWorker {

    private ScheduledThreadPoolExecutor scheduleWorker = ThreadPoolConfig.getInstance().getPublicScheduledExecutor(true);
    @Resource
    private TaskLogService taskLogService;
    @Resource
    private TaskGroupService taskGroupService;



    @Override
    public void start() {
        scheduleWorker.scheduleWithFixedDelay(() -> {
            try {
                List<TaskGroupBO> listByLogRetentionDay = taskGroupService.getListByLogRetentionDay((short) 0);
                listByLogRetentionDay.forEach(taskGroupBO -> {
                    taskLogService.deleteBySpecifiedTime(DateUtils.addDays(new Date(), -taskGroupBO.getLogRetentionDays()));
                });
            } catch (Exception e) {
                log.error("清理日志定时出现异常！!", e);
            }
        }, 0, 1, TimeUnit.DAYS);
    }

    @Override
    public void stop() {
        ThreadPoolRegisterCenter.destroy(scheduleWorker);
    }
}
