package com.maxy.caller.core.mpmc;

import com.lmax.disruptor.RingBuffer;
import com.maxy.caller.core.config.ThreadPoolConfig;
import com.maxy.caller.core.config.ThreadPoolRegisterCenter;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.maxy.caller.core.constant.ThreadConstant.SERVER_RING_BUFFER_THREAD_POOL;

/**
 * @author maxuyang
 */
@Component
@Log4j2
public class RingbufferInvoker {

    private ExecutorService workerPoolExecutor = ThreadPoolConfig.getInstance().getSingleThreadExecutor(true, SERVER_RING_BUFFER_THREAD_POOL);
    private RingBuffer<Event<List<DelayTask>>> ringBuffer = RingBufferFactory.createDisruptor(1024 * 1024, getConsumers(10));
    private Producer producer = new Producer(ringBuffer);

    public void invoke(List<DelayTask> delayTasks) {
        producer.sendData(delayTasks);
    }

    private  Consumer[] getConsumers(int length) {
         Consumer[] consumers = new  Consumer[length];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new  Consumer();
        }
        return consumers;
    }

    @PreDestroy
    public void destroy() {
        ThreadPoolRegisterCenter.destroy(workerPoolExecutor);
    }
}