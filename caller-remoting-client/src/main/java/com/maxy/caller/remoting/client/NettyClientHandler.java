package com.maxy.caller.remoting.client;

import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.remoting.client.helper.NettyClientHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.function.BiFunction;

/**
 * @Author maxy
 **/
@Log4j2
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMsg> {

    @Resource
    private NettyClientHelper nettyClientHelper;

    @PostConstruct
    public void initialize() {
        nettyClientHelper.callerTaskEvent.get();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMsg msg) {
        log.info("客户端读取信息.请求Id:{}", msg.getRequestId());
        BiFunction<ProtocolMsg, Channel, Object> consumer = nettyClientHelper.getEventMap().get(msg.getMsgTypeEnum());
        if (consumer != null) {
            consumer.apply(msg, ctx.channel());
        }
    }
}
