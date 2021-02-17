package com.maxy.caller.core.netty;

import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author maxuyang
 **/
public abstract class AbstractNettyRemoting {

    protected DefaultEventExecutorGroup defaultEventExecutorGroup;

    protected void initDefaultExecutor(String threadName, Integer workerThreads) {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                workerThreads,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format(threadName, this.threadIndex.incrementAndGet()));
                    }
                });
    }

}
