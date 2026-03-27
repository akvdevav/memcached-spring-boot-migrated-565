/*
 * Copyright 2016-2026 Sixhours
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sixhours.memcached.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * {@code SpyMemcachedClient} replaced with Redis implementation.
 *
 * @author Sasa Bolic
 */
public class SpyMemcachedClient implements IMemcachedClient {
    private static final Log log = LogFactory.getLog(SpyMemcachedClient.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public SpyMemcachedClient(RedisTemplate<String, Object> redisTemplate) {
        log.info("Redis client initialized.");
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    @Override
    public RedisTemplate<String, Object> nativeClient() {
        return this.redisTemplate;
    }

    @Override
    public Object get(String key) {
        return this.valueOps.get(key);
    }

    @Override
    public void set(String key, int exp, Object value) {
        if (exp > 0) {
            this.valueOps.set(key, value, Duration.ofSeconds(exp));
        } else {
            this.valueOps.set(key, value);
        }
    }

    @Override
    public void touch(String key, int exp) {
        if (exp > 0) {
            this.redisTemplate.expire(key, Duration.ofSeconds(exp));
        }
    }

    @Override
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    @Override
    public void flush() {
        RedisConnectionFactory factory = this.redisTemplate.getConnectionFactory();
        if (factory != null) {
            factory.getConnection().flushAll();
        }
    }

    @Override
    public long incr(String key, int by) {
        Long result = this.valueOps.increment(key, by);
        return result != null ? result : 0L;
    }

    @Override
    public void shutdown() {
        RedisConnectionFactory factory = this.redisTemplate.getConnectionFactory();
        if (factory != null) {
            try {
                factory.getConnection().close();
            } catch (Exception e) {
                log.warn("Error while shutting down Redis connection", e);
            }
        }
    }
}