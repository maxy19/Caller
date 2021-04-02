package com.maxy.caller.core.mpmc;

import com.lmax.disruptor.RingBuffer;
import com.maxy.caller.pojo.DelayTask;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * @author maxuyang
 */
@Log4j2
public class Producer {

    private RingBuffer<Event<List<DelayTask>>> ringBuffer;

    public Producer(RingBuffer<Event<List<DelayTask>>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void sendData(List<DelayTask> value) {
        long sequence = ringBuffer.next();
        try {
            Event<List<DelayTask>> listEvent = ringBuffer.get(sequence);
            listEvent.setElement(value);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}