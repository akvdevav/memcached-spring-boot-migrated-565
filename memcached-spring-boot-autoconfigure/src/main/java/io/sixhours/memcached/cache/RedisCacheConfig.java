package io.sixhours.memcached.cache;

import java.net.InetSocketAddress;
import java.util.List;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Configuration for Valkey (Redis-compatible) cache manager.
 *
 * Replaces legacy SpyMemcached cache setup with Spring Data Redis.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final MemcachedCacheProperties properties;

    public RedisCacheConfig(MemcachedCacheProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        List<InetSocketAddress> servers = properties.getServers();
        String host = "localhost";
        int port = 6379;
        if (!servers.isEmpty()) {
            InetSocketAddress address = servers.get(0);
            host = address.getHostString();
            port = address.getPort();
        }
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory).build();
    }
}