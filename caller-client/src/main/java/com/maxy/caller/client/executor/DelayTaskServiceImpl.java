package com.maxy.caller.client.executor;

import com.google.common.base.Preconditions;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.remoting.client.helper.NettyClientHelper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author maxy
 **/
@Service
@Log4j2
public class DelayTaskServiceImpl implements DelayTaskService {
    @Resource
    private NettyClientHelper nettyClientHelper;

    @Override
    public boolean send(List<DelayTask> delayTasks) {
        validate(delayTasks);
        while (true) {
            if (nettyClientHelper.getChannel() != null) {
                nettyClientHelper.getChannel().writeAndFlush(ProtocolMsg.toEntity(delayTasks));
                break;
            }
        }
        return true;
    }

    private void validate(List<DelayTask> delayTasks) {
        delayTasks.forEach(delayTask -> {
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getGroupKey()), "groupKey不能为空!");
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getBizKey()), "bizKey不能为空!");
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getTopic()), "topic不能为空!");
            Preconditions.checkArgument(Objects.nonNull(delayTask.getExecutionTime()), "执行时间不能为空!");
            delayTask.setTimeout(Optional.ofNullable(delayTask.getTimeout()).orElse(3000));
        });
    }
}
