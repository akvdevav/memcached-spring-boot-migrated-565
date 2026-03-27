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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * {@code Redis} cache client implementation replacing XMemcached.
 *
 * @author Igor Bolic
 */
public class XMemcachedClient implements IMemcachedClient {
    private static final Log log = LogFactory.getLog(XMemcachedClient.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public XMemcachedClient(RedisTemplate<String, Object> redisTemplate) {
        log.info("XMemcachedClient (Redis) client initialized.");
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    @Override
    public RedisTemplate<String, Object> nativeClient() {
        return this.redisTemplate;
    }

    @Override
    public Object get(String key) {
        try {
            return this.valueOps.get(key);
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to get key", e);
        }
    }

    @Override
    public void set(String key, int exp, Object value) {
        try {
            if (exp > 0) {
                this.valueOps.set(key, value, Duration.ofSeconds(exp));
            } else {
                this.valueOps.set(key, value);
            }
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to set key", e);
        }
    }

    @Override
    public void touch(String key, int exp) {
        try {
            this.redisTemplate.expire(key, Duration.ofSeconds(exp));
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to touch key", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            this.redisTemplate.delete(key);
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to delete key", e);
        }
    }

    @Override
    public void flush() {
        try {
            RedisConnectionFactory factory = this.redisTemplate.getConnectionFactory();
            if (factory != null) {
                RedisConnection connection = factory.getConnection();
                connection.flushAll();
                connection.close();
            }
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to flush all keys", e);
        }
    }

    @Override
    public long incr(String key, int by) {
        try {
            Long result = this.valueOps.increment(key, by);
            return result != null ? result : 0L;
        } catch (DataAccessException e) {
            throw new MemcachedOperationException("Failed to increment key", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            RedisConnectionFactory factory = this.redisTemplate.getConnectionFactory();
            if (factory != null) {
                RedisConnection connection = factory.getConnection();
                connection.close();
            }
        } catch (Exception e) {
            throw new MemcachedOperationException("Failed to shutdown client", e);
        }
    }
}