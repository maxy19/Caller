package com.maxy.caller.admin.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import javax.annotation.Resource;
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

    public Map<String, JedisPool> getNodeMap() {
        return jedisCluster.getClusterNodes();
    }


    public Long zadd(String key, double score, String member) {
        return jedisCluster.zadd(key, score, member);
    }

    public Long lpush(String key, String... value) {
        return jedisCluster.lpush(key, value);
    }


    public List<Object> getQueueData(List<String> keys, List<String> args) {
        String script = "local result = {}\n" +
                "local length = ARGV[1]\n" +
                "local backupQueue = KEYS[1] .. ':backup'\n" +
                "for i = 1, tonumber(length) do\n" +
                "    local key = redis.call('RPOP', KEYS[1])\n" +
                "    if (key) then\n" +
                "        local values = redis.call('ZRANGEBYSCORE', key, ARGV[2], ARGV[3], ARGV[4], ARGV[5], ARGV[6])\n" +
                "        if (#values > 0) then\n" +
                "            for j, v in ipairs(values) do\n" +
                "                local backupValue = redis.call('LPUSH', backupQueue, v);\n" +
                "                redis.call('ZREM', key, v)\n" +
                "            end\n" +
                "            result[i] = values\n" +
                "        end\n" +
                "    end\n" +
                "end\n" +
                "return result";
        log.debug("script:{}", script);
        return (List) jedisCluster.eval(script, keys, args);
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

    public String get(String key) {
        return jedisCluster.get(key);
    }


}
