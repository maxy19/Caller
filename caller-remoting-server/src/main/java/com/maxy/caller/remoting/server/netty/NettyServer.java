package com.maxy.caller.remoting.server.netty;

import com.maxy.caller.common.utils.RemotingUtil;
import com.maxy.caller.core.netty.AbstractNettyRemoting;
import com.maxy.caller.core.netty.config.NettyServerConfig;
import com.maxy.caller.remoting.server.netty.kryo.KryoServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.maxy.caller.core.constant.ThreadConstant.SERVER_HANDLE_THREAD_POOL;

/**
 * @Author maxy
 **/
@Log4j2
@Component
public class NettyServer extends AbstractNettyRemoting {

    @Resource
    private NettyServerConfig nettyServerConfig;
    @Resource
    private NettyServerHandler nettyServerHandler;
    @Resource
    private NettyServerConnHandler nettyServerConnHandler;
    private EventLoopGroup eventLoopGroupSelector;
    private EventLoopGroup eventLoopGroupBoss;
    private InetSocketAddress inetSocketAddress;


    private boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform()
                && nettyServerConfig.isUseEpollNativeSelector()
                && Epoll.isAvailable();
    }

    @PostConstruct
    private void initialize() {
        //初始化地址
        inetSocketAddress = new InetSocketAddress(this.nettyServerConfig.getServerPort());
        //初始化事件
        if (useEpoll()) {
            initEpoll();
        } else {
            initNIO();
        }
        //多线程处理handle
        initDefaultExecutor(SERVER_HANDLE_THREAD_POOL, nettyServerConfig.getServerWorkerThreads());
    }


    /**
     * 初始化nio
     */
    private void initNIO() {
        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("caller-netty-nio-boss_%d", this.threadIndex.incrementAndGet()));
            }
        });

        this.eventLoopGroupSelector = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            private int threadTotal = nettyServerConfig.getServerSelectorThreads();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("caller-netty-nio-selector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
            }
        });
    }

    /**
     * 初始化epoll
     */
    private void initEpoll() {
        this.eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("caller-netty-epoll-boss_%d", this.threadIndex.incrementAndGet()));
            }
        });

        this.eventLoopGroupSelector = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            private int threadTotal = nettyServerConfig.getServerSelectorThreads();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("caller-netty-epoll-selector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
            }
        });
    }

    public Boolean start() {
        //初始化boos 与 selector 根据不同系统选择使用nio 还是 ePoll
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventLoopGroupBoss, eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                /**
                 * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                 * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                 */
                .option(ChannelOption.SO_BACKLOG, 1024)
                /**
                 * 水位线 高于水位线则不会写入 必须等降调低水位线才会继续
                 */
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(nettyServerConfig.getDefaultLowWaterMark(), nettyServerConfig.getDefaultHighWaterMark()))
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
                 * Nagle算法试图减少TCP包的数量和结构性开销, 将多个较小的包组合成较大的包进行发送.但这不是重点,
                 * 关键是这个算法受TCP延迟确认影响, 会导致相继两次向连接发送请求包
                 */
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(inetSocketAddress)
                .childHandler(KryoServerChannelInitializer.builder()
                             .nettyServerHandler(nettyServerHandler)
                             .nettyServerConnHandler(nettyServerConnHandler)
                             .defaultEventExecutorGroup(defaultEventExecutorGroup)
                             .nettyServerConfig(nettyServerConfig)
                             .build());
        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        ChannelFuture cf = null;
        try {
            cf = bootstrap.bind().sync();
            log.info("Caller服务端地址:{}.端口:{}启动完毕!!!", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("服务端出现异常.", e);
            stop(eventLoopGroupSelector, eventLoopGroupBoss);
            return false;
        }
        return true;
    }


    public void stop(EventLoopGroup workerGroup, EventLoopGroup bossGroup) {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        defaultEventExecutorGroup.shutdownGracefully();
    }
}
