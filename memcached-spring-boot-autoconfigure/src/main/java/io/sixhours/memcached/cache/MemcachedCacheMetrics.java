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

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import org.springframework.cache.Cache;

/**
 * Collect metrics on cache instances (e.g., Redis/Valkey caches).
 *
 * @author Mat Mannion
 */
public class MemcachedCacheMetrics extends CacheMeterBinder {
    private final Cache cache;

    /**
     * Creates a new {@link MemcachedCacheMetrics} instance.
     *
     * @param cache     The cache to be instrumented.
     * @param cacheName Will be used to tag metrics with "cache".
     * @param tags      tags to apply to all recorded metrics.
     */
    public MemcachedCacheMetrics(Cache cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
        this.cache = cache;
    }

    @Override
    protected Long size() {
        // Size information is not available for the underlying cache implementation
        return null;
    }

    @Override
    protected long hitCount() {
        // Hit count is not directly available; returning zero as placeholder
        return 0L;
    }

    @Override
    protected Long missCount() {
        // Miss count is not directly available; returning zero as placeholder
        return 0L;
    }

    @Override
    protected Long evictionCount() {
        // Eviction count is not directly available; returning zero as placeholder
        return 0L;
    }

    @Override
    protected long putCount() {
        // Put count is not directly available; returning zero as placeholder
        return 0L;
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        // No implementation‑specific metrics for the generic cache
    }
}