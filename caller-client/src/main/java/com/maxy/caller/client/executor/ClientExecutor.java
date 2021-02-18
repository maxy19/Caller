package com.maxy.caller.client.executor;

import com.google.common.base.Preconditions;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.pojo.RegConfigInfo;
import com.maxy.caller.remoting.client.NettyClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端初始化
 *
 * @Author maxy
 **/
@Log4j2
@Component
public class ClientExecutor implements ApplicationContextAware, SmartInitializingSingleton, ClientWorker {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource
    private NettyClient nettyClient;

    private ExecutorService singleThreadExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true);
    @Resource
    private RegConfigInfo regConfigInfo;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void afterSingletonsInstantiated() {
        start();
    }

    @Override
    public void start() {
        //注册所有的bean
        RegisteredFlowProcessor.process(applicationContext);
        //校验信息
        validate(regConfigInfo);
        //开启netty
        singleThreadExecutor.execute(() -> {
            nettyClient.start(regConfigInfo.getRemoteIp(), regConfigInfo.getRemotePort(), countDownLatch);
        });
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("",e);
        }
    }

    private void validate(RegConfigInfo regConfigInfo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(regConfigInfo.getGroupKey()), "groupKey不能为空!");
        Preconditions.checkArgument(StringUtils.isNotBlank(regConfigInfo.getBizKey()), "appKey不能为空!");
        Preconditions.checkArgument(StringUtils.isNotBlank(regConfigInfo.getRemoteIp()), "remoteIp不能为空!");
        Preconditions.checkArgument(regConfigInfo.getRemotePort() != null, "remotePort不能为空!");
    }

    @Override
    public void stop() {
        //关闭netty-client
        nettyClient.stop();
        //关闭线程
        ThreadPoolRegisterCenter.destroy(singleThreadExecutor);
    }
}
