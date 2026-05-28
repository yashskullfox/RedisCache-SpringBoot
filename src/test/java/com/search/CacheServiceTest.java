package com.search;

import com.search.api.CacheData;
import com.search.api.PatchData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    private Cache cache;
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cache = new ConcurrentMapCache("AccountCache");
        cacheService = new CacheService(cache);
    }

    @Test
    void addDataToCache_storesEntry() throws Exception {
        CacheData data = new CacheData();
        data.setType("savings");
        data.setValue("1000");

        cacheService.addDataToCache(42, data);

        assertNotNull(cacheService.retrieveByCacheKey("Account_42"));
    }

    @Test
    void addDataToCache_doesNotOverwriteExistingEntry() throws Exception {
        CacheData data = new CacheData();
        data.setValue("1000");
        cacheService.addDataToCache(1, data);

        CacheData data2 = new CacheData();
        data2.setValue("9999");
        cacheService.addDataToCache(1, data2);

        CacheInfo stored = (CacheInfo) cacheService.retrieveByCacheKey("Account_1");
        assertEquals(1000L, stored.getValue());
    }

    @Test
    void removeDataFromCache_evictsEntry() throws Exception {
        CacheData data = new CacheData();
        data.setValue("500");
        cacheService.addDataToCache(10, data);

        cacheService.removeDataFromCache(10);

        assertNull(cacheService.retrieveByCacheKey("Account_10"));
    }

    @Test
    void removeDataFromCache_nonExistentKey_doesNotThrow() {
        assertDoesNotThrow(() -> cacheService.removeDataFromCache(9999));
    }

    @Test
    void updateDataInCache_credit_increasesBalance() throws Exception {
        CacheData data = new CacheData();
        data.setValue("100");
        cacheService.addDataToCache(5, data);

        PatchData patch = new PatchData();
        patch.setAction("Credit");
        patch.setValue(50L);
        cacheService.updateDataInCache(5, patch);

        CacheInfo info = (CacheInfo) cacheService.retrieveByCacheKey("Account_5");
        assertEquals(150L, info.getValue());
    }

    @Test
    void updateDataInCache_withdraw_decreasesBalance() throws Exception {
        CacheData data = new CacheData();
        data.setValue("200");
        cacheService.addDataToCache(6, data);

        PatchData patch = new PatchData();
        patch.setAction("Withdraw");
        patch.setValue(75L);
        cacheService.updateDataInCache(6, patch);

        CacheInfo info = (CacheInfo) cacheService.retrieveByCacheKey("Account_6");
        assertEquals(125L, info.getValue());
    }

    @Test
    void updateDataInCache_withdraw_doesNotGoBelowZero() throws Exception {
        CacheData data = new CacheData();
        data.setValue("50");
        cacheService.addDataToCache(7, data);

        PatchData patch = new PatchData();
        patch.setAction("Withdraw");
        patch.setValue(200L);
        cacheService.updateDataInCache(7, patch);

        CacheInfo info = (CacheInfo) cacheService.retrieveByCacheKey("Account_7");
        assertEquals(50L, info.getValue()); // unchanged
    }

    @Test
    void updateDataInCache_remove_evictsEntry() throws Exception {
        CacheData data = new CacheData();
        data.setValue("300");
        cacheService.addDataToCache(8, data);

        PatchData patch = new PatchData();
        patch.setAction("Remove");
        patch.setValue(0L);
        cacheService.updateDataInCache(8, patch);

        assertNull(cacheService.retrieveByCacheKey("Account_8"));
    }
}
