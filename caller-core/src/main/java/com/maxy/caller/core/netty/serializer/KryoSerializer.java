package com.maxy.caller.core.netty.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;

/**
 * @author maxy
 */
@Log4j2
public class KryoSerializer {

    private static final ThreadLocalKryoFactory factory = new ThreadLocalKryoFactory();

    public static ByteBuf serialize(Object object, ByteBuf out) {
        Kryo kryo = factory.getKryo();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos, 16777216);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (Exception e) {
            log.error("serialize出现异常!!", e);
        }
        return out.writeBytes(b);
    }

    public static Object deserialize(ByteBuf in) {
        if (in == null) {
            return null;
        }
        Input input = new Input(new ByteBufInputStream(in), 16777216);
        Kryo kryo = factory.getKryo();
        return kryo.readClassAndObject(input);
    }

}