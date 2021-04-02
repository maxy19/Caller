package com.maxy.caller.core.mpmc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.maxy.caller.pojo.DelayTask;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import static com.maxy.caller.core.constant.ThreadConstant.SERVER_RING_BUFFER_THREAD_POOL;

/**
 * @author maxuyang
 */
public class RingBufferFactory {

    public static RingBuffer<Event<List<DelayTask>>> createDisruptor(Integer ringBufferSize, Consumer[] consumers) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(SERVER_RING_BUFFER_THREAD_POOL).build();
        Disruptor<Event<List<DelayTask>>> disruptor = new Disruptor<>(() -> new Event<>(), ringBufferSize,
                threadFactory, ProducerType.SINGLE,
                new YieldingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(consumers);
        return disruptor.start();
    }
}
