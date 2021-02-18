package com.maxy.caller.sample.test;


import com.google.common.collect.Lists;
import com.maxy.caller.common.utils.DateUtils;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.pojo.DelayTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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
        List<DelayTask> list = Lists.newArrayList();
        DelayTask delayTask = null;
        for (int i = 0; i <2 ; i++) {
            delayTask = new DelayTask();
            delayTask.setGroupKey("taobao");
            delayTask.setBizKey("order");
            delayTask.setTopic("clsExpireOrder");
            delayTask.setExecutionTime(DateUtils.addSecond(30));
            delayTask.setExecutionParam("触发成功!!");
            delayTask.setTimeout(4000);
            delayTask.setRetryNum((byte) 1);
            list.add(delayTask);
            Thread.sleep(1000);
        }
        delayTaskService.send(list);
        System.in.read();
    }


} 
