package com.maxy.caller.core.netty.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoClassRegistrator {

    void register(Kryo kryo);

}
