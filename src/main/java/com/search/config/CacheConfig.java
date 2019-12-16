package com.search.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CacheConfig {

    public static final String ACCOUNT_CACHE = "AccountCache";

    public static final List<String> CACHE_LIST = Collections.unmodifiableList(Arrays.asList(ACCOUNT_CACHE));

    //Cache Bucket
    @Bean(name = ACCOUNT_CACHE)
    public Cache accountCache(CacheManager cacheManager){
        return cacheManager.getCache(ACCOUNT_CACHE);
    }
}
