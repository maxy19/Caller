package com.maxy.caller.admin.manager;

import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.admin.worker.LogCleanWorker;
import com.maxy.caller.admin.worker.MonitorAlarmWorker;
import com.maxy.caller.admin.worker.RegistryWorker;
import com.maxy.caller.admin.worker.ScheduleWorker;
import com.maxy.caller.admin.worker.TriggerWorker;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.utils.SpringContextUtils;
import com.maxy.caller.remoting.server.netty.NettyServer;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * admin流程管理器
 *
 * @Author maxy
 **/
@Component
public class AdminFlowManager implements SmartInitializingSingleton, AdminWorker {

    private List<AdminWorker> workerList = new ArrayList();
    @Resource
    private NettyServer nettyServer;

    private ExecutorService nettyStartService = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true);

    @Override
    public void afterSingletonsInstantiated() {
        nettyStartService.execute(() -> {
            //启动netty服务端
            nettyServer.start();
        });
        //装载组件
        workerList.add(SpringContextUtils.getBean(TriggerWorker.class));
        workerList.add(SpringContextUtils.getBean(ScheduleWorker.class));
        workerList.add(SpringContextUtils.getBean(MonitorAlarmWorker.class));
        workerList.add(SpringContextUtils.getBean(LogCleanWorker.class));
        workerList.add(SpringContextUtils.getBean(RegistryWorker.class));
        //启动组件
        start();
    }


    @Override
    public void start() {
        //启动
       /* workerList.forEach(worker -> {
            worker.start();
        });*/
       workerList.get(0).start();
       workerList.get(1).start();
    }

    @Override
    public void stop() {
        workerList.forEach(worker -> {
            worker.stop();
        });
    }
}
