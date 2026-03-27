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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.io.IOException;

/**
 * Auto-configuration for Redis (Valkey) cache.
 * Creates {@link CacheManager} when caching is enabled via {@link EnableCaching}.
 *
 * @author Igor Bolic
 * @author Sasa Bolic
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisConnectionFactory.class, CacheManager.class})
@EnableConfigurationProperties(RedisCacheProperties.class)
@AutoConfigureAfter(name = "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration")
public class AppEngineMemcachedCacheAutoConfiguration {

    @Configuration
    @ConditionalOnRefreshScope
    static class RefreshableMemcachedCacheConfiguration {

        @Bean
        @RefreshScope
        @ConditionalOnMissingBean(value = RedisCacheManager.class, search = SearchStrategy.CURRENT)
        public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) throws IOException {
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                    .build();
        }
    }

    @Configuration
    @ConditionalOnMissingRefreshScope
    static class MemcachedCacheConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = RedisCacheManager.class, search = SearchStrategy.CURRENT)
        public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) throws IOException {
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                    .build();
        }
    }
}