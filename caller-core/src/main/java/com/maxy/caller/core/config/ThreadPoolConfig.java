package com.maxy.caller.core.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.maxy.caller.core.service.CommonService;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author maxy
 **/
@Log4j2
public class ThreadPoolConfig implements CommonService {

    private ThreadPoolConfig() {
    }

    private static final class InnerClass {
        private static ThreadPoolConfig INSTANCE = new ThreadPoolConfig();
    }

    public static ThreadPoolConfig getInstance() {
        return InnerClass.INSTANCE;
    }

    public ExecutorService getPublicThreadPoolExecutor(boolean isDaemon) {
        return getPublicThreadPoolExecutor(isDaemon, "public-thread-pool-executor");
    }

    public ExecutorService getPublicThreadPoolExecutor(String threadName) {
        return getPublicThreadPoolExecutor(true, threadName);
    }

    public ScheduledThreadPoolExecutor getPublicScheduledExecutor(boolean isDaemon) {
        return getPublicScheduledExecutor(CORE_SIZE, isDaemon);
    }

    public ScheduledThreadPoolExecutor getPublicScheduledExecutor(Integer corePoolSize, boolean isDaemon) {
        return getPublicScheduledExecutor(corePoolSize, isDaemon, "public-scheduled-executor");
    }

    public ScheduledThreadPoolExecutor getPublicScheduledExecutor(boolean isDaemon, String threadName) {
        return getPublicScheduledExecutor(CORE_SIZE, isDaemon, threadName);
    }

    public ExecutorService getSingleThreadExecutor(boolean isDaemon) {
        return getSingleThreadExecutor(true, "single-thread-pool-executor");
    }

    public ExecutorService getSingleThreadExecutor(String threadName) {
        return getSingleThreadExecutor(true, threadName);
    }

    /**
     * 公共单线程执行
     *
     * @return
     */
    public ExecutorService getSingleThreadExecutor(boolean isDaemon, String threadName) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
                getThreadFactory(threadName, isDaemon),
                (r, executor) -> {
                    log.warn(String.join("#", threadName, "队列已经满员,正在调用拒绝策略!"));
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                });
        return ThreadPoolRegisterCenter.register(threadPoolExecutor, 10);
    }

    /**
     * 公共的Scheduled定时任务
     *
     * @return
     */
    public ScheduledThreadPoolExecutor getPublicScheduledExecutor(Integer corePoolSize, boolean isDaemon, String threadName) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(corePoolSize,
                getThreadFactory(threadName, isDaemon), (r, executor) -> {
            log.warn(String.join("#", threadName, "队列已经满员,正在调用拒绝策略!"));
            if (!executor.isShutdown()) {
                r.run();
            }
        });
        return (ScheduledThreadPoolExecutor) ThreadPoolRegisterCenter.register(scheduledThreadPoolExecutor, 120);
    }

    /**
     * 公共的ThreadPool定时任务
     *
     * @return
     */
    public ExecutorService getPublicThreadPoolExecutor(boolean isDaemon, String threadName) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
                getThreadFactory(threadName, isDaemon),
                (r, executor) -> {
                    log.warn(String.join("#", threadName, "队列已经满员,正在调用拒绝策略!"));
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                });
        return ThreadPoolRegisterCenter.register(threadPoolExecutor, 120);
    }

    /**
     * 公共的ThreadPool定时任务
     *
     * @return
     */
    public ExecutorService getPublicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, boolean isDaemon, String threadName) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
                getThreadFactory(threadName, isDaemon),
                (r, executor) -> {
                    log.warn(String.join("#", threadName, "队列已经满员,正在调用拒绝策略!"));
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                });
        return ThreadPoolRegisterCenter.register(threadPoolExecutor, 120);
    }

    /**
     * 公共的ThreadPool定时任务
     *
     * @return
     */
    public ExecutorService getPublicThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                                       boolean isDaemon, String threadName, boolean isInitThreadPool) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
                getThreadFactory(threadName, isDaemon),
                (r, executor) -> {
                    log.warn(String.join("#", threadName, "队列已经满员,正在调用拒绝策略!"));
                    if (!executor.isShutdown()) {
                        r.run();
                    }
                });
        if (isInitThreadPool) {
            threadPoolExecutor.prestartCoreThread();
        }
        return ThreadPoolRegisterCenter.register(threadPoolExecutor, 120);
    }


    public ThreadFactory getThreadFactory(String threadName, boolean isDaemon) {
        return new ThreadFactoryBuilder().setDaemon(isDaemon).setNameFormat(threadName + "-%d").build();
    }


}
