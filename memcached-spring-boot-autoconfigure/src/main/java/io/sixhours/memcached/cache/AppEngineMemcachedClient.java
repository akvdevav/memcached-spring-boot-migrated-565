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
 * WITHOUT WARRANTIES OR ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sixhours.memcached.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.time.Duration;

/**
 * {@code AppEngine} memcached client implementation replaced with Valkey (Redis) support.
 *
 * @author Igor Bolic
 */
public class AppEngineMemcachedClient implements IMemcachedClient {
    private static final Log log = LogFactory.getLog(AppEngineMemcachedClient.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public AppEngineMemcachedClient(RedisTemplate<String, Object> redisTemplate) {
        log.info("AppEngineMemcachedClient client initialized with RedisTemplate.");
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
        this.valueOps.set(key, value, Duration.ofSeconds(exp));
    }

    @Override
    public void touch(String key, int exp) {
        this.redisTemplate.expire(key, Duration.ofSeconds(exp));
    }

    @Override
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    @Override
    public void flush() {
        if (this.redisTemplate.getConnectionFactory() != null) {
            this.redisTemplate.getConnectionFactory().getConnection().flushDb();
        }
    }

    @Override
    public long incr(String key, int by) {
        Long result = this.valueOps.increment(key, by);
        return result != null ? result : 0L;
    }

    @Override
    public void shutdown() {
        // No explicit shutdown needed for RedisTemplate; resources are managed by Spring.
    }
}