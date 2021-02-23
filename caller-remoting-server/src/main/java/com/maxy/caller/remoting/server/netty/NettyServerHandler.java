package com.maxy.caller.remoting.server.netty;

import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.remoting.server.netty.helper.NettyServerHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @Author maxy
 * 生命周期 ：handlerAdded -> channelRegistered
 * channelActive -> channelRead -> channelReadComplete
 * channelInactive -> channelUnRegistered -> handlerRemoved
 **/
@Log4j2
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    @Resource
    private NettyServerHelper nettServerHelper;

    @PostConstruct
    public void initialize() {
        nettServerHelper.pingerEvent.get();
        nettServerHelper.registryEvent.get();
        nettServerHelper.resultEvent.get();
        nettServerHelper.delayTaskEvent.get();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("handlerAdded#handler被添加到channel的pipeline.客户端地址:{}", ctx.channel().remoteAddress());
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("channelRegistered#channel注册到NioEventLoop.客户端地址:{}", ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("channelUnregistered#channel取消和NioEventLoop的绑定.客户端地址:{}", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive#channel准备就绪.客户端地址:{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive#channel断开连接.客户端地址:{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ProtocolMsg) {
            ProtocolMsg protocolMsg = (ProtocolMsg) msg;
            log.info("channelRead0#channel中有可读的数据.客户端地址:{}", ctx.channel().remoteAddress());
            BiConsumer<ProtocolMsg, Channel> consumer = nettServerHelper.getEventMap().get(protocolMsg.getMsgTypeEnum());
            if (Objects.nonNull(consumer)) {
                consumer.accept(protocolMsg, ctx.channel());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelReadComplete#channel读数据完成.客户端地址:{}", ctx.channel().remoteAddress());
        super.channelReadComplete(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("handlerRemoved#handler从channel的pipeline中移除.客户端地址:{}", ctx.channel().remoteAddress());
        super.handlerRemoved(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = Optional.ofNullable(event.state()).map(ele -> event.state().name()).orElse(Strings.EMPTY);
            log.warn("客户端心跳超时[远程地址:{},超时类型:{}]", ctx.channel().remoteAddress(), type);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.debug("connect#服务端正在连接:{}.", parse(remoteAddress));
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.debug("disconnect#服务端执行断开连接:{}", parse(ctx.channel().remoteAddress()));
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        log.debug("close#服务端执行关闭连接:{}", parse(ctx.channel().remoteAddress()));
        nettServerHelper.getIpChannelMapping().remove(parse(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.debug("close#服务端发现异常!!", cause);
        ctx.close();
        nettServerHelper.getIpChannelMapping().remove(parse(ctx.channel()));
    }
}
