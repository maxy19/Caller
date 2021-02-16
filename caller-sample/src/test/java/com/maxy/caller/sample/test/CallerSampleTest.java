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

/**
 * CallerSample Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>1�� 31, 2021</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CallerSampleTest {

    @Resource
    private DelayTaskService delayTaskService;

    @Test
    public void testSend() throws Exception {
        DelayTask delayTask = new DelayTask();
        delayTask.setGroupKey("taobao");
        delayTask.setBizKey("order");
        delayTask.setTopic("clsExpireOrder");
        delayTask.setExecutionTime(DateUtils.addMinutes(10));
        delayTask.setExecutionParam("你好测试成功!!");
        delayTask.setTimeout(4000);
        delayTask.setRetryNum((byte) 1);

        DelayTask delayTask2 = new DelayTask();
        delayTask2.setGroupKey("taobao");
        delayTask2.setBizKey("order");
        delayTask2.setTopic("clsExpireOrder");
        delayTask2.setExecutionTime(DateUtils.addMinutes(10));
        delayTask2.setExecutionParam("你好测试成功!!");
        delayTask2.setTimeout(3000);
        delayTask2.setRetryNum((byte) 1);
        delayTaskService.send(Lists.newArrayList(delayTask,delayTask2));
        System.in.read();
    }


} 
