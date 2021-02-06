package com.maxy.caller.admin.cache;

import com.google.common.collect.ImmutableList;
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

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        return jedisCluster.zadd(key, scoreMembers);
    }

    public Long sadd(String key, String value) {
        return jedisCluster.sadd(key, value);
    }

    public Long lpush(String key, String value) {
        return jedisCluster.lpush(key, value);
    }

    public List<Object> getIndexData(String key, String limit) {
        String indexScript = " local result = {} " +
                " local list = redis.call('LRANGE',KEYS[1],0,ARGV[1]) " +
                " result[1] = #list " +
                " for i, key in pairs(list) do " +
                "   local groupName = redis.call('RPOPLPUSH',list,KEY[1],'_backup') " +
                "   table.insert(result,i+1,groupName) " +
                " end " +
                " return result ";
        log.debug("indexScript:{}", indexScript);
        return (List) jedisCluster.eval(indexScript, ImmutableList.of(key), ImmutableList.of(limit));
    }

    public List<Object> getQueueData(String key, List<String> args) {
        String scriptWithDelayNum =
                "local result = {} " +
                        //获取需要执行的字典索引
                        //"local member = redis.call('SMEMBERS', KEYS[1]) " +
                        //填充索引在下标1的位置
                        //"result[1]=member " +
                        //遍历索引
                        "for i, key in pairs(member) do " +
                        //从原始zSet获取数据
                        "  local valueArr = redis.call('ZRANGEBYSCORE',key,ARGV[1],ARGV[2],ARGV[3],ARGV[4],ARGV[5]) " +
                        //放到备份队列
                        "  redis.call('LPUSH',key,'_backup',valueArr) " +
                        //删除原队列
                        "  redis.call('ZREM',key,value) " +
                        //填充返回值在大于1的位置
                        "  table.insert(result,i+1,valueArr)  " +
                        "end " +
                        " return result ";
        log.debug("scriptWithDelayNum:{}", scriptWithDelayNum);
        return (List) jedisCluster.eval(scriptWithDelayNum, ImmutableList.of(key), args);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return jedisCluster.zrangeByScoreWithScores(key, min, max);
    }

    public Set<String> smembers(String key) {
        return jedisCluster.smembers(key);
    }

    public Long zrem(String key, String... member) {
        return jedisCluster.zrem(key, member);
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
