package com.maxy.caller.core.service;

import java.util.List;
import java.util.Map;

/**
 * @Author maxuyang
 **/
public interface Cache {

    Long zadd(String key, double score, String member);

    List<String> getQueueDataByBackup(List<String> keys, List<String> args);

    List<Object> getQueueData(List<String> keys, List<String> args);

    Long expire(String key, int expireTime);

    String set(String key, int expire, String value);

    void hmset(String key, Map<String, String> hash, int expire);

    void hmset(String key, String field, String value, int expire);

    String hget(String key, String field);

    String get(String key);

    long lrem(String key, String arg);
}
