package com.maxy.caller.core.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcHolder {

    public static final Map<Long, Byte> REQUEST_MAP = new ConcurrentHashMap();
}