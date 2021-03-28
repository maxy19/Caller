package com.maxy.caller.core.netty.serializer.kryo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

/**
 * @author maxy
 */
@Log4j2
public class KryoEncode extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        DefaultKryoContext.get().serialze(msg,out);
        ctx.flush();
    }
}
