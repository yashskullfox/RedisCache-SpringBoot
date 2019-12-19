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

    private Cache cache;

    public CacheService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void addDataToCache(int accountNumber, CacheData cacheData) throws Exception {

        /* this will Add the account/CacheKey in Cache to store the value
         * It will create the entry in Cache Buket */

        String cacheKey = "Account_" + accountNumber;
        long value = Long.valueOf(cacheData.getValue());
        if(retrieveByCacheKey(cacheKey) == null) {
            cache.put(cacheKey, new CacheInfo(accountNumber,
                    cacheData.getType(),
                    value,
                    Instant.now()));
        }
    }


    @Override
    public void removeDataFromCache(int accountNumber) throws Exception {
            String cacheKey = "Account_" + accountNumber;
        if(retrieveByCacheKey(cacheKey) != null) {
            cache.evict(cacheKey);
        }
    }

    @Override
    public void updateDataInCache(int accountNumber, PatchData patchData) throws Exception {
        String cacheKey = "Account_" + accountNumber;
        switch (patchData.getAction()) {
            case "Credit":

                /* this will Increase the value in Cache stored values
                 * And then add it back to Cache Data*/

                if (cacheKey != null) {
                    Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
                    if (valueWrapper != null) {
                        CacheInfo cacheInfo =
                                (CacheInfo) valueWrapper.get();
                        long obj1 = cacheInfo.getValue();
                        long obj2 = patchData.getValue();
                        long sumOfValue = Long.sum(obj1, obj2);
                        cacheInfo.setValue(sumOfValue);
                    }
                }
                break;
            case "Withdraw":

                /* this will subtract the value from Cache stored values
                * And then add it back to Cache Data*/

                if (cacheKey != null) {
                    Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
                    if (valueWrapper != null) {
                        CacheInfo cacheInfo =
                                (CacheInfo) valueWrapper.get();
                        long obj1 = cacheInfo.getValue();
                        long obj2 = patchData.getValue();
                        long sumOfValue = Math.subtractExact(obj1, obj2);
                        //if account balance goes less then 0 it will not value in cache will not update
                        if (sumOfValue > 0){
                            cacheInfo.setValue(sumOfValue);
                        }else {
                            cacheInfo.setValue(cacheInfo.getValue());
                        }
                    }
                }
                break;
            case "Remove":

                /* this will remove the account/CacheKey from Cache stored values */

                if(retrieveByCacheKey(cacheKey) != null) {
                    cache.evict(cacheKey);
                }
        }
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
