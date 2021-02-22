package com.maxy.caller.core.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author maxy
 */
public class ThreadLocalKryoFactory extends KryoFactory {

    private final ThreadLocal<Kryo> holder = ThreadLocal.withInitial(() -> createKryo());

    public Kryo getKryo() {
        return holder.get();
    }
}
    
