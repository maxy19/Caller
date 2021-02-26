package com.maxy.caller.client.executor;

import com.google.common.base.Preconditions;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.remoting.client.helper.NettyClientHelper;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.maxy.caller.core.utils.CallerUtils.getMonitor;

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
        Channel channel = nettyClientHelper.getChannel();
        if (Objects.isNull(channel)) {
            log.error("send#无法获取channel信息.");
            return false;
        }
        if (!channel.isWritable()) {
            log.error("send#超过水位线无法发送服务端信息.");
            return false;
        }
        channel.writeAndFlush(ProtocolMsg.toEntity(delayTasks), getMonitor(channel));
        log.info("delayTask任务发送:{}!!", channel);
        return true;
    }

    private void validate(List<DelayTask> delayTasks) {
        delayTasks.forEach(delayTask -> {
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getGroupKey()), "groupKey不能为空!");
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getBizKey()), "bizKey不能为空!");
            Preconditions.checkArgument(StringUtils.isNotBlank(delayTask.getTopic()), "topic不能为空!");
            Preconditions.checkArgument(Objects.nonNull(delayTask.getExecutionTime()), "执行时间不能为空!");
            delayTask.setTimeout(Optional.ofNullable(delayTask.getTimeout()).orElse(3000));
            delayTask.setExecutionParam(Optional.ofNullable(delayTask.getExecutionParam()).orElse(Strings.EMPTY));
            delayTask.setRetryNum(Optional.ofNullable(delayTask.getRetryNum()).orElse((byte) 0));
        });
    }
}
