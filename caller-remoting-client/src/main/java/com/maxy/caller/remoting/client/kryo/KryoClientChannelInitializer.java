package com.maxy.caller.remoting.client.kryo;

import com.maxy.caller.core.netty.config.NettyClientConfig;
import com.maxy.caller.core.netty.serializer.kryo.KryoDecode;
import com.maxy.caller.core.netty.serializer.kryo.KryoEncode;
import com.maxy.caller.remoting.client.NettyClientConnHandler;
import com.maxy.caller.remoting.client.NettyClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.Builder;

import java.util.concurrent.TimeUnit;

/**
 * @author maxuyang
 */
@Builder
public class KryoClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private NettyClientHandler nettyClientHandler;
    private NettyClientConnHandler nettyClientConnHandler;
    private NettyClientConfig nettyClientConfig;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(defaultEventExecutorGroup,
                        new KryoEncode(),
                        new KryoDecode(),
                        new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds(), TimeUnit.SECONDS),
                        new ReadTimeoutHandler(3 * nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                        nettyClientConnHandler,
                        nettyClientHandler);
    }
}
