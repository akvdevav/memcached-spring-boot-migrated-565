package io.sixhours.redis.cache;

import io.sixhours.redis.cache.RedisCacheProperties.Authentication;
import io.sixhours.redis.cache.RedisCacheProperties.Authentication.Mechanism;
import io.sixhours.redis.cache.RedisCacheProperties.HashStrategy;
import io.sixhours.redis.cache.RedisCacheProperties.Provider;

import java.time.Duration;
import java.util.List;

import static io.sixhours.redis.cache.RedisCacheProperties.Protocol;
import static java.util.Collections.singletonList;

/**
 * Default cache configuration values for Valkey (Redis).
 *
 * @author Igor Bolic
 */
public final class Default {

    public static final List<String> SERVERS = singletonList("localhost:6379");

    public static final Authentication AUTHENTICATION = new Authentication();

    public static final Mechanism AUTHENTICATION_MECHANISM = Mechanism.PASSWORD;

    public static final Provider PROVIDER = Provider.STATIC;

    public static final int EXPIRATION = 0;

    public static final String PREFIX = "redis:spring-boot";

    public static final String NAMESPACE = "namespace";

    public static final Protocol PROTOCOL = Protocol.STANDARD;

    public static final long OPERATION_TIMEOUT = 2500L;

    public static final Duration SERVERS_REFRESH_INTERVAL = Duration.ofMinutes(1);

    public static final HashStrategy HASH_STRATEGY = HashStrategy.STANDARD;

    private Default() {
        throw new AssertionError("Suppress default constructor");
    }
}