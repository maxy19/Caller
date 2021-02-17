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
public class CallerOrderSampleTest {

    @Resource
    private DelayTaskService delayTaskService;

    @Test
    public void testSend() throws Exception {
        for (int i = 0; i <10 ; i++) {
            DelayTask delayTask = new DelayTask();
            delayTask.setGroupKey("taobao");
            delayTask.setBizKey("order");
            delayTask.setTopic("sendMsg");
            delayTask.setExecutionTime(DateUtils.addSecond(2));
            delayTask.setExecutionParam("你好测试成功!!");
            delayTask.setTimeout(4000);
            delayTask.setRetryNum((byte) 1);
            delayTaskService.send(Lists.newArrayList(delayTask));
            Thread.sleep(1000);
        }
        System.in.read();
    }


} 
