package com.maxy.caller.remoting.client;

import com.maxy.caller.core.netty.AbstractNettyRemoting;
import com.maxy.caller.core.netty.config.NettyClientConfig;
import com.maxy.caller.core.netty.serializer.kryo.KryoDecode;
import com.maxy.caller.core.netty.serializer.kryo.KryoEncode;
import com.maxy.caller.pojo.RegConfigInfo;
import com.maxy.caller.pojo.Value;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.maxy.caller.core.constant.ThreadConstant.CLIENT_HANDLE_THREAD_POOL;

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
        initDefaultExecutor(CLIENT_HANDLE_THREAD_POOL, nettyClientConfig.getClientWorkerThreads());
    }

    public void start(List<RegConfigInfo.AddressInfo> addressInfos, CountDownLatch countDownLatch) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(nettyClientConfig.getDefaultLowWaterMark(), nettyClientConfig.getDefaultHighWaterMark()))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(defaultEventExecutorGroup, new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds(), TimeUnit.SECONDS))
                                //加码
                                .addLast(defaultEventExecutorGroup, "encoder", new KryoEncode())
                                //解码
                                .addLast(defaultEventExecutorGroup, "decoder", new KryoDecode())
                                .addLast(defaultEventExecutorGroup, nettyClientHandler);

                    }
                });
        try {
            this.bootstrap = bootstrap;
            for (RegConfigInfo.AddressInfo addressInfo : addressInfos) {
                boolean isConnected = connection(countDownLatch, bootstrap, addressInfo);
                if (isConnected) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Caller客户端链接出现异常!!", e);
            stop();
        }
    }

    /**
     * @param countDownLatch
     * @param bootstrap
     * @param addressInfo
     * @return
     * @throws InterruptedException
     */
    private boolean connection(CountDownLatch countDownLatch, Bootstrap bootstrap, RegConfigInfo.AddressInfo addressInfo) {
        Value<Boolean> flag = new Value<>(true);
        try {
            ChannelFuture cf = bootstrap.connect(addressInfo.getIp(), Integer.parseInt(addressInfo.getPort())).sync();
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    log.info("Caller客户端链接服务器成功!");
                    countDownLatch.countDown();
                } else {
                    log.warn("Caller客户端链接服务器失败! 异常:{}", cf.cause().getMessage());
                    eventExecutors.shutdownGracefully(); //关闭线程组
                    flag.setValue(false);
                }
            });
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("连接地址:{}出现异常!!!", addressInfo, e);
            flag.setValue(false);
        }
        return flag.getValue();
    }

    public void stop() {
        if (Objects.nonNull(eventExecutors)) {
            log.warn("Caller客户端即将关闭.");
            eventExecutors.shutdownGracefully();
        }
        defaultEventExecutorGroup.shutdownGracefully();
    }

}
