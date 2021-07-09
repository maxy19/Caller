package com.maxy.caller.remoting.server.netty.kryo;

import com.maxy.caller.core.netty.config.NettyServerConfig;
import com.maxy.caller.core.netty.serializer.kryo.KryoDecode;
import com.maxy.caller.core.netty.serializer.kryo.KryoEncode;
import com.maxy.caller.remoting.server.netty.NettyServerConnHandler;
import com.maxy.caller.remoting.server.netty.NettyServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.Builder;

import java.util.concurrent.TimeUnit;

/**
 * @author maxy
 */
@Builder
public class KryoServerChannelInitializer extends ChannelInitializer {

    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private NettyServerHandler nettyServerHandler;
    private NettyServerConnHandler nettyServerConnHandler;
    private NettyServerConfig nettyServerConfig;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        //心跳检测
        ch.pipeline().addLast(defaultEventExecutorGroup,
                new KryoEncode(),
                new KryoDecode(),
                new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds(), TimeUnit.SECONDS),
                new ReadTimeoutHandler(3 * nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                nettyServerConnHandler,
                nettyServerHandler);
    }
}
