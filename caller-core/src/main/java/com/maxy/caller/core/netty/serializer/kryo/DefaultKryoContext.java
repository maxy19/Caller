package com.maxy.caller.core.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.maxy.caller.core.netty.protocol.ProtocolMsg;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.BitSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.RegexSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.Message;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Log4j2
public class DefaultKryoContext {

    private static final int DEFAULT_BUFFER = 1024 * 100;

    private KryoPool pool;

    private static DefaultKryoContext defaultKryoContext;

    public static DefaultKryoContext getInstance(KryoClassRegistrator registrator) {
        if (defaultKryoContext == null) {
            synchronized (DefaultKryoContext.class) {
                if (defaultKryoContext == null) {
                    defaultKryoContext = new DefaultKryoContext(registrator);
                }
            }
        }
        return defaultKryoContext;
    }

    private DefaultKryoContext(KryoClassRegistrator registrator) {
        KryoFactory factory = new KryoFactoryImpl(registrator);
        pool = new KryoPool.Builder(factory).build();
    }

    private static class KryoFactoryImpl implements KryoFactory {
        private KryoClassRegistrator registrator;

        public KryoFactoryImpl(KryoClassRegistrator registrator) {
            this.registrator = registrator;
        }

        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            registrator.register(kryo);
            return kryo;
        }
    }


    public ByteBuf serialze(Object obj, ByteBuf out) {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos, 1024 * 1024 * 100);
        kryo.writeClassAndObject(output, obj);
        output.flush();
        output.close();
        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (Exception e) {
            log.error("serialize出现异常!!", e);
        }
        pool.release(kryo);
        return out.writeBytes(b);
    }


    public Object deserialze(ByteBuf in) {
        if (in == null) {
            return null;
        }
        Input input = null;
        Kryo kryo = pool.borrow();
        try {
            input = new Input(new ByteBufInputStream(in));
            return kryo.readClassAndObject(input);
        } finally {
            pool.release(kryo);
            if (input != null) {
                input.close();
            }
        }
    }

    public static DefaultKryoContext get() {
        // kryo pool context.
        return DefaultKryoContext.getInstance(kryo -> {
            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            kryo.register(Message.class);
            kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
            kryo.register(InvocationHandler.class, new JdkProxySerializer());
            kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
            kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
            kryo.register(Pattern.class, new RegexSerializer());
            kryo.register(BitSet.class, new BitSetSerializer());
            kryo.register(URI.class, new URISerializer());
            kryo.register(UUID.class, new UUIDSerializer());
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
            SynchronizedCollectionsSerializer.registerSerializers(kryo);
            kryo.register(HashMap.class);
            kryo.register(ArrayList.class);
            kryo.register(LinkedList.class);
            kryo.register(HashSet.class);
            kryo.register(TreeSet.class);
            kryo.register(Hashtable.class);
            kryo.register(Date.class);
            kryo.register(Calendar.class);
            kryo.register(ConcurrentHashMap.class);
            kryo.register(SimpleDateFormat.class);
            kryo.register(GregorianCalendar.class);
            kryo.register(Vector.class);
            kryo.register(BitSet.class);
            kryo.register(StringBuffer.class);
            kryo.register(StringBuilder.class);
            kryo.register(Object.class);
            kryo.register(Object[].class);
            kryo.register(String[].class);
            kryo.register(byte[].class);
            kryo.register(char[].class);
            kryo.register(int[].class);
            kryo.register(float[].class);
            kryo.register(double[].class);
            kryo.register(ProtocolMsg.class);
        });
    }

}
