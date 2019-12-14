package com.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Value("${redis.url:localhost}")
    private String redisHostName;

    @Value("${redis.port:6379}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(redisHostName, redisPort));
    }

    @Bean
    public CacheManager cacheManager(JedisConnectionFactory redisConnectionFactory) {
        final RedisCacheConfiguration defaultCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues();
        final Map<String, RedisCacheConfiguration> cacheConfigurationMap =
                redisConfiguration(defaultCacheConfiguration);

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .cacheDefaults(defaultCacheConfiguration)
                .disableCreateOnMissingCache()
                .build();

    }

    private Map<String, RedisCacheConfiguration> redisConfiguration(
            RedisCacheConfiguration defaultCacheConfiguration) {
        Map<String, RedisCacheConfiguration> redisCacheConfigurations = new HashMap<>();
        final RedisCacheConfiguration oneHour = defaultCacheConfiguration.entryTtl(Duration.ofHours(1));

        redisCacheConfigurations.put(CacheConfig.ACCOUNT_CACHE, oneHour);

        return Collections.unmodifiableMap(redisCacheConfigurations);
    }

}
