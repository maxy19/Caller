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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public void sendTask() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                List<DelayTask> list = new ArrayList<>();
                DelayTask delayTask = new DelayTask();
                delayTask.setGroupKey("taobao");
                delayTask.setBizKey("order");
                delayTask.setTopic("clsExpireOrder");
                delayTask.setExecutionTime(LocalDateUtils.plus(LocalDateTime.now(), 5, ChronoUnit.SECONDS));
                delayTask.setExecutionParam("触发成功!!");
                delayTask.setTimeout(4000);
                delayTask.setRetryNum((byte) 1);
                list.add(delayTask);
                delayTaskService.send(list);
            });
        }
        System.out.println(System.currentTimeMillis()-start+" ms");
        System.in.read();
    }
}
