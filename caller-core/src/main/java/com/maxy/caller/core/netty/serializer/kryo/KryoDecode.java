package com.maxy.caller.core.netty.serializer.kryo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.log4j.Log4j2;


/**
 * @author maxy
 */
@Log4j2
public class KryoDecode extends LengthFieldBasedFrameDecoder {

    public KryoDecode() {
        this(1048576);
    }

    public KryoDecode(int maxObjectSize) {
        /**
         * maxFrameLength：最大帧长度。也就是可以接收的数据的最大长度。如果超过，此次数据会被丢弃。
         * lengthFieldOffset：长度域偏移。就是说数据开始的几个字节可能不是表示数据长度，需要后移几个字节才是长度域。
         * lengthFieldLength：长度域字节数。用几个字节来表示数据长度。
         * lengthAdjustment：数据长度修正。因为长度域指定的长度可以使header+body的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么我们需要修正数据长度。
         */
        super(maxObjectSize, 6, 4, 0, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return DefaultKryoContext.deserialze(in);
    }
}
