package com.maxy.caller.core.service;

import java.util.List;

/**
 * @Author maxuyang
 **/
public interface Cache {

    Long zadd(String key, double score, String member);

    List<String> getQueueDataByBackup(List<String> keys, List<String> args);

    List<Object> getQueueData(List<String> keys, List<String> args);

    Long expire(String key, int expireTime);

    String set(String key, int expire, String value);
}
