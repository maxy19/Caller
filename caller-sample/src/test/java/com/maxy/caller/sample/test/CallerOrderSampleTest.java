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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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

    private AtomicInteger count = new AtomicInteger();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void sendTask() throws Exception {
        int total = 2;
        long start = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            count.incrementAndGet();
            new Thread(() -> {
                List<DelayTask> list = new ArrayList<>();
                DelayTask delayTask = new DelayTask();
                delayTask.setGroupKey("taobao");
                delayTask.setBizKey("order");
                delayTask.setTopic("clsExpireOrder");
                delayTask.setExecutionTime(LocalDateUtils.plus(LocalDateTime.now(), 10, ChronoUnit.SECONDS));
                delayTask.setExecutionParam("触发成功!!");
                delayTask.setTimeout(4000);
                delayTask.setRetryNum((byte) 1);
                list.add(delayTask);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                delayTaskService.send(list);
            }).start();
        }
        while (count.get() == total){
            Thread.sleep(10);
            countDownLatch.countDown();
            break;
        }
        System.out.println(System.currentTimeMillis() - start + " ms");
        System.in.read();
    }
}
