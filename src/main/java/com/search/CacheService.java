package com.search;

import api.CacheData;
import api.DataService;
import api.PatchData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;


import java.time.Instant;

@Service(value = "CacheService")
public class CacheService implements DataService {

    private DataService dataService;
    private Cache cache;

    public CacheService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void addDataToCache(int accountNumber, CacheData cacheData) throws Exception {
        String cacheKey = "Account_" + accountNumber;
        if(retrieveByCacheKey(cacheKey) == null) {
            cache.put(cacheKey, new CacheInfo(accountNumber,
                    cacheData.getType(),
                    cacheData.getValue(),
                    Instant.now()));
        }
    }


    @Override
    public void removeDataFromCache(int accountNumber) throws Exception {

    }

    @Override
    public void updateDataInCache(int accountNumber, PatchData patchData) throws Exception {
    }


    public Object retrieveByCacheKey(String cacheKey){
        if (StringUtils.isNoneBlank(cacheKey)) {
            Cache.ValueWrapper wrapper = cache.get(cacheKey);
            if (wrapper != null) {
                return wrapper.get();
            }
        }
        return null;
    }

}
