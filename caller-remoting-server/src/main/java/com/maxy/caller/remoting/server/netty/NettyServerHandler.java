package com.maxy.caller.remoting.server.netty;

import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.function.BiConsumer;

@Log4j2
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<ProtocolMsg> {

    @Resource
    private NettyServerHelper nettServerHelper;

    @PostConstruct
    public void initialize() {
        nettServerHelper.heartbeatEvent.get();
        nettServerHelper.registryEvent.get();
        nettServerHelper.resultEvent.get();
        nettServerHelper.delayTaskEvent.get();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMsg msg) throws Exception {
        ProtocolMsg protocolMsg = (ProtocolMsg) msg;
        log.debug("channelRead0#channel中有可读的数据.客户端地址:{}", ctx.channel().remoteAddress());
        BiConsumer<ProtocolMsg, Channel> consumer = nettServerHelper.getEventMap().get(protocolMsg.getMsgTypeEnum());
        if (Objects.nonNull(consumer)) {
            consumer.accept(protocolMsg, ctx.channel());
        }
    }

}
