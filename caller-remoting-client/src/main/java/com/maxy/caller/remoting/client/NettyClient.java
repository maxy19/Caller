package com.maxy.caller.remoting.client;

import com.maxy.caller.core.netty.AbstractNettyRemoting;
import com.maxy.caller.core.netty.KryoDecode;
import com.maxy.caller.core.netty.KryoEncode;
import com.maxy.caller.core.netty.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author maxy
 **/
@Log4j2
@Component
public class NettyClient extends AbstractNettyRemoting {

    EventLoopGroup eventExecutors = new NioEventLoopGroup();
    @Resource
    private NettyClientConfig nettyClientConfig;
    @Resource
    private NettyClientHandler nettyClientHandler;

    private Bootstrap bootstrap;

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    @PostConstruct
    private void initialize() {
        //多线程处理handle
        initDefaultExecutor("caller-netty-client-handle-thread_%d", nettyClientConfig.getClientWorkerThreads());
    }

    public void start(String inetHost, Integer inetPort, CountDownLatch countDownLatch) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(defaultEventExecutorGroup, new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS))
                                //加码
                                .addLast(defaultEventExecutorGroup, "encoder", new KryoEncode())
                                //解码
                                .addLast(defaultEventExecutorGroup, "decoder", new KryoDecode())
                                .addLast(defaultEventExecutorGroup, nettyClientHandler);
                    }
                });
        try {
            this.bootstrap = bootstrap;
            ChannelFuture cf = bootstrap.connect(inetHost, inetPort).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    log.info("Caller客户端链接服务器成功!");
                    countDownLatch.countDown();
                } else {
                    log.warn("Caller客户端链接服务器失败! 异常:{}", cf.cause().getMessage());
                    eventExecutors.shutdownGracefully(); //关闭线程组
                }
            });
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Caller客户端链接出现异常!!", e);
            stop();
        }
    }

    public void stop() {
        if (Objects.nonNull(eventExecutors)) {
            log.warn("Caller客户端即将关闭.");
            eventExecutors.shutdownGracefully();
        }
        defaultEventExecutorGroup.shutdownGracefully();
    }

}
