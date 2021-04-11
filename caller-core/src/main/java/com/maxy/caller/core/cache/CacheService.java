package com.maxy.caller.core.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * redis缓存操作类
 *
 * @author maxy
 */
@Log4j2
@Component
public class CacheService {

    @Resource(name = "callerRedisCluster")
    private JedisCluster jedisCluster;

    public int getMasterNodeSize() {
        return jedisCluster.getClusterNodes().size() / 2;
    }


    public Long zadd(String key, double score, String member) {
        return jedisCluster.zadd(key, score, member);
    }

    public Long lpush(String key, String... value) {
        return jedisCluster.lpush(key, value);
    }

    public List<String> getQueueDataByBackup(List<String> keys, List<String> args) {
        String script = "local result = {}\n" +
                "local length = ARGV[1]\n" +
                "for i = 1, tonumber(length) do\n" +
                "    local value = redis.call('RPOP', KEYS[1])\n" +
                "    if (value) then\n" +
                "        table.insert(result, value)\n" +
                "    end\n" +
                "end\n" +
                "return result\n";
        return (List<String>) jedisCluster.eval(script, keys, args);
    }


    public List<Object> getQueueData(List<String> keys, List<String> args) {
        String script = "local result = {}\n" +
                "local values = redis.call('ZRANGEBYSCORE', KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4], ARGV[5])\n" +
                "if (#values > 0) then\n" +
                "    for i, v in ipairs(values) do\n" +
                "        local value = redis.call('LPUSH', KEYS[2], v)\n" +
                "        if (value) then\n" +
                "            redis.call('ZREM', KEYS[1], v)\n" +
                "        end\n" +
                "    end\n" +
                "    result[1] = values\n" +
                "end\n" +
                "return result";
        return (List<Object>) jedisCluster.eval(script, keys, args);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return jedisCluster.zrangeByScoreWithScores(key, min, max);
    }

    public Long expire(String key, int expireTime) {
        return jedisCluster.expire(key, expireTime);
    }

    public String set(String key, int expire, String value) {
        return jedisCluster.setex(key, expire, value);
    }

    public void hmset(String key, Map<String, String> hash, int expire) {
        jedisCluster.hmset(key, hash);
        expire(key, expire);
    }

    public void hmset(String key, String field, String value, int expire) {
        Map<String, String> map = new HashMap<>();
        map.put(field, value);
        jedisCluster.hmset(key, map);
        expire(key, expire);
    }

    public String hget(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    public String get(String key) {
        return jedisCluster.get(key);
    }

    public long lrem(String key, String arg) {
        return jedisCluster.lrem(key, 1, arg);
    }
}
