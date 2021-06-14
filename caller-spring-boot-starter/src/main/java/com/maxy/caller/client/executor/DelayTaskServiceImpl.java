package com.maxy.caller.client.executor;

import com.google.common.base.Preconditions;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.core.service.DelayTaskService;
import com.maxy.caller.core.utils.CallerUtils;
import com.maxy.caller.pojo.DelayTask;
import com.maxy.caller.pojo.RetryConfig;
import com.maxy.caller.pojo.Value;
import com.maxy.caller.remoting.client.helper.NettyClientHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
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

    @SneakyThrows
    @Override
    public boolean send(List<DelayTask> delayTasks) {
         return send(delayTasks,null);
    }

    @Override
    public boolean send(List<DelayTask> delayTasks, RetryConfig retryConfig) throws InterruptedException {
        validate(delayTasks);
        Channel channel = nettyClientHelper.getChannel();
        if (Objects.isNull(channel)) {
            log.error("send#无法获取channel信息.");
            return false;
        }
        monitor(delayTasks, retryConfig, channel, sendTask(delayTasks, channel));
        return true;
    }

    private ChannelFuture sendTask(List<DelayTask> delayTasks, Channel channel) throws InterruptedException {
        ChannelFuture channelFuture;
        if (!channel.isWritable()) {
            log.error("send#超过水位线无法发送服务端信息.");
            channelFuture = channel.writeAndFlush(ProtocolMsg.toEntity(delayTasks)).sync();
        } else {
            channelFuture = channel.writeAndFlush(ProtocolMsg.toEntity(delayTasks));
        }
        return channelFuture;
    }

    /**
     * 增加重试机制
     * @param delayTasks
     * @param retryConfig
     * @param channel
     * @param channelFuture
     */
    private void monitor(List<DelayTask> delayTasks, RetryConfig retryConfig, Channel channel, ChannelFuture channelFuture) {
        if (!CallerUtils.monitor(channelFuture)) {
            if(Objects.nonNull(retryConfig)) {
                Value<Boolean> flag = new Value<>(false);
                for (int retryNum = 1; retryNum <= retryConfig.getRetryNum() ; retryNum++) {
                    channel.eventLoop().schedule(() -> {
                        try {
                            if(CallerUtils.monitor(sendTask(delayTasks,channel))) {
                                flag.setValue(true);
                            }
                        } catch (InterruptedException e) {
                            log.error("sync连接出现异常!!",e);
                        }
                    }, retryConfig.getDelayTime(), retryConfig.getDelayTimeUnit());
                    //执行成功则停止
                    if (flag.getValue()) {
                        log.info("任务重试第{}次后发送成功!!",retryNum);
                        return;
                    }
                }
                if(!flag.getValue()){
                    log.error("任务重试{}次依然发送失败,请具体查看下问题!!!", retryConfig.getRetryNum());
                }
            }
        }
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
