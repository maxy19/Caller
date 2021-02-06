package com.maxy.caller.core.config;


import com.maxy.caller.pojo.MethodModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maxy
 */
public class DispatchCenter {

    private static Map<String, MethodModel> DispatchFactory = new ConcurrentHashMap();

    public static MethodModel get(String key) {
        return DispatchFactory.get(key);
    }

    public static void put(String key, MethodModel value) {
        DispatchFactory.put(key, value);
    }

}
