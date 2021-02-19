package com.maxy.caller.core.netty.config;

import lombok.Data;
import org.springframework.stereotype.Component;


/**
 * @author maxy
 */
@Data
@Component
public class NettyServerConfig {

    private int serverPort = 8888;
    private int serverWorkerThreads = 8;
    private int serverSelectorThreads = 3;
    private int serverChannelMaxIdleTimeSeconds = 120;
    private int serverSocketSndBufSize = 65535;
    private int serverSocketRcvBufSize = 65535;
    private int serverReaderIdleTime = 100;
    private boolean serverPooledByteBufAllocatorEnable = true;
    private boolean useEpollNativeSelector = false;
    private int defaultLowWaterMark = 32 * 1024;
    private int defaultHighWaterMark = 64 * 1024;
}
