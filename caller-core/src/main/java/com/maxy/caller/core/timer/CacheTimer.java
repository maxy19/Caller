package com.maxy.caller.core.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author maxy
 */
public class CacheTimer {
    private HashedWheelTimer hashedWheelTimer;

    public CacheTimer() {
        hashedWheelTimer = new HashedWheelTimer(new ThreadFactory() {
            AtomicInteger threadCount = new AtomicInteger();
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setPriority(Thread.NORM_PRIORITY + 3);
                thread.setName("timer-wheel-thread-pool-" + threadCount.incrementAndGet());
                return thread;
            }
        }, 100L, TimeUnit.MILLISECONDS, 512);
    }

    public static CacheTimer getEntity() {
        return new CacheTimer();
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
