package com.maxy.caller.core.common;

import com.maxy.caller.core.netty.protocol.ProtocolMsg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcHolder {

    public static final Map<String,RpcFuture<ProtocolMsg>> REQUEST_MAP = new ConcurrentHashMap();
}