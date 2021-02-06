package com.maxy.caller.persistent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnClass({JedisCluster.class})
public class RedisClusterConfig {


    @Value("${spring.redis.cluster.nodes:null}")
    private String clusterNodes;

    @Value("${spring.redis.commandTimeout:5000}")
    private Integer commandTimeout;

    @Bean("callerRedisCluster")
    public JedisCluster assistantRedisCluster() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(100);
        poolConfig.setMinIdle(30);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(1000L);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(60000L);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000L);
        poolConfig.setNumTestsPerEvictionRun(-1);


        String[] serverArray = clusterNodes.split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        return new JedisCluster(nodes, commandTimeout,poolConfig);
    }

}