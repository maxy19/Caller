package com.maxy.caller.sample.test;


import com.maxy.caller.common.utils.LocalDateUtils;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.pojo.DelayTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * CallerSample Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>1�� 31, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CallerOrderSampleTest {

    @Resource
    private DelayTaskService delayTaskService;

    @Test
    public void testSend() throws Exception {
        List<DelayTask> list = new ArrayList<>();
        DelayTask delayTask = null;
        for (int i = 0; i <10; i++) {
            delayTask = new DelayTask();
            delayTask.setGroupKey("taobao");
            delayTask.setBizKey("order");
            delayTask.setTopic("clsExpireOrder");
            delayTask.setExecutionTime(LocalDateUtils.plus(LocalDateTime.now(),10,ChronoUnit.SECONDS));
            delayTask.setExecutionParam("触发成功!!");
            delayTask.setTimeout(4000);
            delayTask.setRetryNum((byte) 1);
            list.add(delayTask);
        }
        System.out.println(list);
        delayTaskService.send(list);
        System.in.read();
    }
}
