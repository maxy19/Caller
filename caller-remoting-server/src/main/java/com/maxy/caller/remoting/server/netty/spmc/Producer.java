package com.maxy.caller.remoting.server.netty.spmc;

import com.lmax.disruptor.RingBuffer;
import com.maxy.caller.pojo.DelayTask;
import io.netty.channel.Channel;
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

    public void sendData(List<DelayTask> value, Channel channel) {
        long sequence = ringBuffer.next();
        try {
            Event<List<DelayTask>> event = ringBuffer.get(sequence);
            event.setElement(value);
            event.setChannel(channel);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}