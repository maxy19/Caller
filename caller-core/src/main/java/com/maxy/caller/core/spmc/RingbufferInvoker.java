package com.maxy.caller.core.spmc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import static com.maxy.caller.core.constant.ThreadConstant.SERVER_RING_BUFFER_THREAD_POOL;

/**
 * @author maxuyang
 */
@Component
@Log4j2
public class RingbufferInvoker implements ApplicationContextAware {

    private Disruptor<Event<List<DelayTask>>> disruptor = null;
    private Producer producer = null;
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        producer = new Producer(create(2048, getConsumers(10)));
    }


    public void invoke(List<DelayTask> delayTasks, String address) {
        producer.sendData(delayTasks, address);
    }

    private Consumer[] getConsumers(int length) {
        Consumer[] consumers = new Consumer[length];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = context.getBean(Consumer.class);
        }
        return consumers;
    }

    public RingBuffer<Event<List<DelayTask>>> create(Integer ringBufferSize, Consumer[] consumers) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(SERVER_RING_BUFFER_THREAD_POOL).build();
        Disruptor<Event<List<DelayTask>>> disruptor = new Disruptor<>(Event::new, ringBufferSize,
                threadFactory, ProducerType.SINGLE,
                new YieldingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(consumers);
        return disruptor.start();
    }

    @PreDestroy
    public void destroy() {
        if (disruptor != null) {
            disruptor.shutdown();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}