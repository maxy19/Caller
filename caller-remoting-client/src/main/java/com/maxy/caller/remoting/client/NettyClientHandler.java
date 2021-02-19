package com.maxy.caller.remoting.client;

import com.maxy.caller.core.netty.pojo.Pinger;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import com.maxy.caller.pojo.RegConfigInfo;
import com.maxy.caller.remoting.client.helper.NettyClientHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static com.maxy.caller.core.utils.CallerUtils.parse;

/**
 * @Author maxy
 **/
@Log4j2
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    @Resource
    private RegConfigInfo regConfigInfo;
    @Resource
    private NettyClientHelper nettyClientHelper;
    @Resource
    private NettyClient nettyClient;

    @PostConstruct
    public void initialize() {
        nettyClientHelper.callerTaskEvent.get();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("handlerAdded#handler被添加到channel的pipeline");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("handlerRemoved#handler从channel的pipeline中移除");
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("channelRegistered#channel注册到NioEventLoop");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
            throws Exception {
        log.debug("channelUnregistered#channel取消和NioEventLoop的绑定");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive#channel被关闭");
        super.channelInactive(ctx);
        //尝试连接...
        doConnect(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = Optional.ofNullable(event.state()).map(ele -> event.state().name()).orElse(Strings.EMPTY);
            log.warn("服务端心跳超时[远程地址:{},超时类型:{}]", ctx.channel().remoteAddress(), type);
            ping(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nettyClientHelper.setChannel(ctx.channel());
        //发送注册信息
        ctx.writeAndFlush(ProtocolMsg.toEntity(regConfigInfo));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发现异常!!", cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolMsg protocolMsg = (ProtocolMsg) msg;
        log.info("客户端读取信息:{}", protocolMsg);
        BiFunction<ProtocolMsg, Channel, Object> consumer = nettyClientHelper.getEventMap().get(protocolMsg.getMsgTypeEnum());
        if (consumer != null) {
            consumer.apply(protocolMsg, ctx.channel());
        }
    }

    @SneakyThrows
    private void ping(Channel channel) {
        if (channel.isActive()) {
            ProtocolMsg<Pinger> protocolMsg = ProtocolMsg.toEntity(System.currentTimeMillis(), regConfigInfo.getUniqName());
            channel.writeAndFlush(protocolMsg);
        } else {
            channel.closeFuture();
        }
    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.debug("connect#客户端正在执行连接:{}.", parse(remoteAddress));
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.debug("disconnect#客户端执行断开连接:{}", parse(ctx.channel().remoteAddress()));
        super.disconnect(ctx, promise);
    }

    public void doConnect(Channel channel) {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = nettyClient.getBootstrap().connect(channel.remoteAddress());
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                log.info("客户端连接服务端:{}成功!!!", parse(channel.remoteAddress()));
            } else {
                log.info("客户端连接服务端失败!!稍后将重试连接:{}!!!", parse(channel.remoteAddress()));
                futureListener.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        doConnect(channel);
                    }
                }, 5, TimeUnit.SECONDS);
            }
        });
        return;
    }
}
