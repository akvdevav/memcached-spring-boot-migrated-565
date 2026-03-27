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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the Redis cache
 * backed by Lettuce client.
 * Creates {@link CacheManager} when caching is enabled via {@link EnableCaching}.
 *
 * @author Igor Bolic
 * @author Sasa Bolic
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RedisConnectionFactory.class, CacheManager.class})
@Conditional(NotAppEngineProviderCondition.class)
@EnableConfigurationProperties(MemcachedCacheProperties.class) // retained for external config compatibility
public class XMemcachedCacheAutoConfiguration {

    @Configuration
    @ConditionalOnRefreshScope
    static class RefreshableRedisCacheConfiguration {

        @Bean
        @RefreshScope
        @ConditionalOnMissingBean(value = CacheManager.class, search = SearchStrategy.CURRENT)
        public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) throws IOException {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        }
    }

    @Configuration
    @ConditionalOnMissingRefreshScope
    static class RedisCacheConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = CacheManager.class, search = SearchStrategy.CURRENT)
        public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) throws IOException {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        }
    }
}