package com.maxy.caller.remoting.server.netty;

import com.maxy.caller.core.netty.KryoDecode;
import com.maxy.caller.core.netty.KryoEncode;
import com.maxy.caller.core.netty.config.NettyServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author maxy
 **/
@Log4j2
@Component
public class NettyServer {

    @Resource
    private NettyServerConfig nettyServerConfig;
    @Resource
    private NettyServerHandler nettyServerHandler;

    public Boolean start(InetAddress localAddress, Integer port) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            AtomicInteger threadCount = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("caller-netty-boss-thread-%d", threadCount.incrementAndGet()));
            }
        });
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                /**
                 * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                 * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                 */
                .option(ChannelOption.SO_BACKLOG, 1024)
                /**
                 * netty提供了IdleStateHandler来检测心跳所以下面不需要
                 */
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                /**
                 * 这个参数表示允许重复使用本地地址和端口。
                 * 比如某个进程非正常退出，该程序占用的端口可能要被占用一段时间才能允许其他进程使用，而且程序死掉以后，
                 * 内核一需要一定的时间才能够释放此端口，不设置SO_REUSEADDR就无法正常使用该端口。
                 */
                .childOption(ChannelOption.SO_REUSEADDR, true)
                /**
                 * 发送的缓存流大小
                 */
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                /**
                 * 接收的缓存流大小
                 */
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                /**
                 * 禁用了Nagle算法 如果开启则会造成网络延迟因为此算法是将包数量与大小积攒到一定量再发送
                 */
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        //心跳检测
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS))
                                //加码
                                .addLast("encoder", new KryoEncode())
                                //解码
                                .addLast("decoder", new KryoDecode())
                                //自定义处理
                                .addLast(nettyServerHandler);
                    }
                });
        ChannelFuture cf = null;
        try {
            cf = bootstrap.bind(localAddress, port).sync();
            log.info("Caller服务端启动完毕!!! 服务器地址:{}.端口:{}", localAddress, port);
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("服务端出现异常.", e);
            stop(workerGroup, bossGroup);
            return false;
        }
        return true;
    }

    public void stop(EventLoopGroup workerGroup, EventLoopGroup bossGroup) {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
