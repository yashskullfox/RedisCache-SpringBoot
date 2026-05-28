package com.search;

import com.search.api.CacheData;
import com.search.api.Request;
import com.search.api.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private Cache cache;
    private CacheService cacheService;
    private SearchService searchService;

    @BeforeEach
    void setUp() throws Exception {
        cache = new ConcurrentMapCache("AccountCache");
        cacheService = new CacheService(cache);
        searchService = new SearchService(cache);

        CacheData data = new CacheData();
        data.setType("checking");
        data.setValue("5000");
        cacheService.addDataToCache(100, data);
    }

    @Test
    void search_existingAccount_returnsResult() throws Exception {
        Request req = new Request();
        req.setAccount("100");

        Result result = searchService.search(req);

        assertEquals(100, result.getAccount());
        assertEquals("checking", result.getType());
        assertEquals(5000L, result.getValue());
        assertNotNull(result.getLastModification());
    }

    @Test
    void search_nonExistingAccount_returnsEmptyResult() throws Exception {
        Request req = new Request();
        req.setAccount("9999");

        Result result = searchService.search(req);

        assertNotNull(result);
        assertEquals(0, result.getAccount()); // default int value
        assertNull(result.getValue());
    }
}
