package com.maxy.caller.core.netty;

import com.maxy.caller.core.netty.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author maxy
 */
public class KryoEncode extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        KryoSerializer.serialize(msg, out);
        ctx.flush();
    }
}
