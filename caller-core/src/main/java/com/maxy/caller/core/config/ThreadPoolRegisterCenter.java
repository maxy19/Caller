package com.maxy.caller.core.config;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author maxy
 */
@Log4j2
public class ThreadPoolRegisterCenter {
    /**
     * @desc 线程池容器
     */
    private static final Map<ExecutorService, Integer> THREAD_POOL_CONTEXT = Maps.newConcurrentMap();


    /**
     * @param executorService
     * @desc 添加线程池到容器中
     */
    public static ExecutorService register(ExecutorService executorService, Integer timeout) {
        THREAD_POOL_CONTEXT.put(executorService, timeout);
        return executorService;
    }

    /**
     * 关闭
     */
    public static void destroy(ExecutorService ...executorServices) {
        for (ExecutorService executorService : executorServices) {
            Integer timeout = THREAD_POOL_CONTEXT.get(executorService);
            MoreExecutors.shutdownAndAwaitTermination(executorService, timeout, TimeUnit.SECONDS);
            THREAD_POOL_CONTEXT.remove(executorService);
        }
    }
}