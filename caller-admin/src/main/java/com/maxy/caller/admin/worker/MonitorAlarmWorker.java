package com.maxy.caller.admin.worker;

import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskLogBO;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.service.MailService;
import com.maxy.caller.core.service.TaskBaseInfoService;
import com.maxy.caller.core.service.TaskLogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.maxy.caller.admin.enums.AlarmStatusEnum.ALARM_FAIL;
import static com.maxy.caller.admin.enums.AlarmStatusEnum.ALARM_SUCCESS;
import static com.maxy.caller.admin.enums.AlarmStatusEnum.DEFAULT;
import static com.maxy.caller.admin.enums.AlarmStatusEnum.LOCK;
import static com.maxy.caller.admin.enums.AlarmStatusEnum.NO_ALARM;

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
    @Resource
    private TaskBaseInfoService taskBaseInfoService;
    @Resource
    private MailService mailService;

    @Override
    public void start() {
        scheduleWorker.scheduleWithFixedDelay(() -> {
            try {
                List<TaskLogBO> failLog = taskLogService.findFailLog();
                failLog.forEach(taskLogBO -> {
                    String toAddress = taskBaseInfoService.getAlarmEmail(taskLogBO.getGroupKey(), taskLogBO.getBizKey(), taskLogBO.getTopic());
                    if (StringUtils.isNotBlank(toAddress)) {
                        boolean isLock = taskLogService.updateAlarmStatus(taskLogBO, DEFAULT.getCode(), LOCK.getCode());
                        if (isLock) {
                            try {
                                mailService.sendSimpleMail(toAddress, String.format("%s调用方法失败.", getUniqueName(taskLogBO)),
                                        String.format("服务端调用远程方法失败,执行地址:%s,执行时间:%s,执行参数:%s,原因：%s",
                                                taskLogBO.getExecutorAddress(),
                                                taskLogBO.getExecutorTime(),
                                                taskLogBO.getExecuteParam(),
                                                taskLogBO.getExecutorResultMsg()));
                            } catch (Exception e) {
                                log.error("sendSimpleMail#发送邮件出现异常!!", e);
                                taskLogService.updateAlarmStatus(taskLogBO, LOCK.getCode(), ALARM_FAIL.getCode());
                            }
                            taskLogService.updateAlarmStatus(taskLogBO, LOCK.getCode(), ALARM_SUCCESS.getCode());
                        }
                    } else {
                        //没有填写邮件的 设置为不需要发送
                        taskLogService.updateAlarmStatus(taskLogBO, DEFAULT.getCode(), NO_ALARM.getCode());
                    }
                });
            } catch (Exception e) {
                log.error("监控报警定时出现异常!!", e);
            }
        }, 2, 60, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        ThreadPoolRegisterCenter.destroy(scheduleWorker);
    }
}
