package com.maxy.caller.core.netty.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author maxy
 */
@Data
@Component
public class NettyClientConfig {

    private int serverPort = 8888;
    private int clientWorkerThreads = 4;
    private int clientCallbackExecutorThreads = Runtime.getRuntime().availableProcessors();
    private int connectTimeoutMillis = 3000;
    private long channelNotActiveInterval = 1000 * 60;
    private int clientChannelMaxIdleTimeSeconds = 120;
    private int clientSocketSndBufSize = 1024 * 1024;
    private int clientSocketRcvBufSize = 1024 * 1024;
    private boolean clientPooledByteBufAllocatorEnable = false;
    private boolean clientCloseSocketIfTimeout = false;
    private int defaultLowWaterMark = 32 * 1024;
    private int defaultHighWaterMark = 64 * 1024 * 1024;
}
