package io.sixhours.memcached.cache;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;
import org.springframework.boot.actuate.metrics.cache.RedisCacheMetrics;
import org.springframework.data.redis.cache.RedisCache;

/**
 * Redis {@link CacheMeterBinderProvider}.
 */
public class MemcachedCacheMeterBinderProvider implements CacheMeterBinderProvider<RedisCache> {

    @Override
    public CacheMeterBinder getMeterBinder(RedisCache redisCache, Iterable<Tag> tags) {
        return new RedisCacheMetrics(redisCache, redisCache.getName(), tags);
    }
}