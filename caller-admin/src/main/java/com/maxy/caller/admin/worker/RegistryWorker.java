package com.maxy.caller.admin.worker;

import com.google.common.collect.Lists;
import com.maxy.caller.admin.service.AdminWorker;
import com.maxy.caller.bo.TaskGroupBO;
import com.maxy.caller.bo.TaskRegistryBO;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.core.netty.config.NettyServerConfig;
import com.maxy.caller.core.service.TaskGroupService;
import com.maxy.caller.core.service.TaskRegistryService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maxy.caller.core.enums.AddressTypeEnum.AUTO_UPLOAD;
import static com.maxy.caller.core.enums.GroupStatusEnum.LOCK;
import static com.maxy.caller.core.enums.GroupStatusEnum.UN_LOCK;

/**
 * 定时更新taskGroup表addressList
 *
 * @Author maxy
 **/
@Component
@Log4j2
public class RegistryWorker implements AdminWorker {

    @Resource
    private NettyServerConfig nettyServerConfig;
    @Resource
    private TaskGroupService taskGroupService;
    @Resource
    private TaskRegistryService taskRegistryService;

    private ScheduledThreadPoolExecutor scheduleWorker = ThreadPoolConfig.getInstance().getPublicScheduledExecutor(true);

    @Override
    public void start() {
        scheduleWorker.scheduleWithFixedDelay(() -> {
            try {
                //查询所有的task-group非手填的
                List<TaskGroupBO> addressList = taskGroupService.getAllListByAddressType(AUTO_UPLOAD.getCode());
                //加锁
                addressList = lockByNeedUpdate(addressList);
                //赋值taskGroup地址列表
                assAddressList(addressList);
                //解锁
                unLockAndUpdate(addressList);
            } catch (Exception e) {
                log.error("更新注册地址定时出现异常！！", e);
            }
        }, 0, nettyServerConfig.getServerReaderIdleTime() * 3, TimeUnit.SECONDS);
    }

    private void assAddressList(List<TaskGroupBO> addressList) {
        //查询所有的task-registry
        List<TaskRegistryBO> allRegistry = taskRegistryService.getAllRegistry();
        //获取最新地址
        addressList.forEach(address -> {
            List<String> collection = Lists.newArrayList();
            allRegistry.forEach(reg -> {
                if (Objects.equals(address.getGroupKey(), reg.getGroupKey())
                        && Objects.equals(address.getBizKey(), reg.getBizKey())) {
                    collection.add(reg.getRegistryAddress());
                }
            });
            address.setAddressList(StringUtils.join(collection, ","));
        });
    }

    private void unLockAndUpdate(List<TaskGroupBO> addressList) {
        addressList.forEach(address -> {
            TaskGroupBO taskGroupBO = taskGroupService.updateStatus(address, LOCK.getCode(), UN_LOCK.getCode());
            if (Objects.isNull(taskGroupBO)) {
                log.debug("未解锁成功!参数:{}", address);
            }
        });
    }

    private List<TaskGroupBO> lockByNeedUpdate(List<TaskGroupBO> addressList) {
        addressList = addressList.stream().filter(address -> {
            TaskGroupBO taskGroupBO = taskGroupService.updateStatus(address, UN_LOCK.getCode(), LOCK.getCode());
            if (Objects.isNull(taskGroupBO)) {
                log.debug("未加锁成功!.参数:{}", address);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return addressList;
    }

    @Override
    public void stop() {
        ThreadPoolRegisterCenter.destroy(scheduleWorker);
    }
}
