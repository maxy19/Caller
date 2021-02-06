package com.maxy.caller.core.netty.serializer;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author maxy
 */
public class ThreadLocalKryoFactory extends KryoFactory {

    private final ThreadLocal<Kryo> holder = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return createKryo();
        }
    };

    public Kryo getKryo() {
        return holder.get();
    }
}
    
