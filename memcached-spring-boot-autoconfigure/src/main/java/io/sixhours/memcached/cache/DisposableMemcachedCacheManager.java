package io.sixhours.memcached.cache;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Disposable {@link RedisCacheManager} bean adapted from the original Memcached implementation.
 */
class DisposableMemcachedCacheManager extends RedisCacheManager implements DisposableBean {

    private final RedisConnectionFactory redisConnectionFactory;

    public DisposableMemcachedCacheManager(RedisConnectionFactory redisConnectionFactory) {
        super(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
              RedisCacheConfiguration.defaultCacheConfig());
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void destroy() throws Exception {
        if (redisConnectionFactory instanceof DisposableBean) {
            ((DisposableBean) redisConnectionFactory).destroy();
        }
    }
}