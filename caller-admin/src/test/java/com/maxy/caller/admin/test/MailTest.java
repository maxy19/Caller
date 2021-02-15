package com.maxy.caller.admin.test;

import com.maxy.caller.core.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author maxuyang
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Resource
    private MailService mailService;

    @Test
    public void test() {
        mailService.sendSimpleMail("315802767@qq.com", "test", "test1234");
    }
}
