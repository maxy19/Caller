package com.maxy.caller.core.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 单例获取hashedwheeltimer
 * @author maxy
 */
public class CacheTimer {
    private HashedWheelTimer hashedWheelTimer;
    private static class Instance {
        private static CacheTimer cacheTimer = new CacheTimer();
    }
    private CacheTimer() {
        hashedWheelTimer = new HashedWheelTimer(new ThreadFactory() {
            AtomicInteger threadCount = new AtomicInteger();
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("timer-wheel-thread-pool-" + threadCount.incrementAndGet());
                return thread;
            }
        }, 100L, TimeUnit.MILLISECONDS, 1024);
    }
    public static CacheTimer getInstance() {
        return Instance.cacheTimer;
    }

    /**
     * 添加延迟定时任务
     * @param task
     * @param delay
     * @param timeUnit
     */
    public void newTimeout(TimerTask task, long delay, TimeUnit timeUnit) {
        hashedWheelTimer.newTimeout(task, delay, timeUnit);
    }
}
