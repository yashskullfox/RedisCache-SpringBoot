package com.search;

import com.search.api.CacheData;
import com.search.api.DataService;
import com.search.api.PatchData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service(value = "CacheService")
public class CacheService implements DataService {

    private final Cache cache;

    public CacheService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void addDataToCache(int accountNumber, CacheData cacheData) throws Exception {
        String cacheKey = "Account_" + accountNumber;
        long value = Long.parseLong(cacheData.getValue());
        if (retrieveByCacheKey(cacheKey) == null) {
            cache.put(cacheKey, new CacheInfo(accountNumber, cacheData.getType(), value, Instant.now()));
        }
    }

    @Override
    public void removeDataFromCache(int accountNumber) throws Exception {
        String cacheKey = "Account_" + accountNumber;
        if (retrieveByCacheKey(cacheKey) != null) {
            cache.evict(cacheKey);
        }
    }

    @Override
    public void updateDataInCache(int accountNumber, PatchData patchData) throws Exception {
        String cacheKey = "Account_" + accountNumber;
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper == null) return;

        CacheInfo cacheInfo = (CacheInfo) valueWrapper.get();
        switch (patchData.getAction()) {
            case "Credit" -> cacheInfo.setValue(Long.sum(cacheInfo.getValue(), patchData.getValue()));
            case "Withdraw" -> {
                long result = Math.subtractExact(cacheInfo.getValue(), patchData.getValue());
                // Floor at current balance; no overdraft
                if (result > 0) cacheInfo.setValue(result);
            }
            case "Remove" -> cache.evict(cacheKey);
        }
    }

    public Object retrieveByCacheKey(String cacheKey) {
        if (StringUtils.isNoneBlank(cacheKey)) {
            Cache.ValueWrapper wrapper = cache.get(cacheKey);
            if (wrapper != null) {
                return wrapper.get();
            }
        }
        return null;
    }
}

