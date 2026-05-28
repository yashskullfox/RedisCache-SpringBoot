package com.search;

import com.search.api.Request;
import com.search.api.Result;
import com.search.api.Service;
import org.springframework.cache.Cache;

@org.springframework.stereotype.Service("SearchService")
public class SearchService implements Service {

    private final Cache cache;

    public SearchService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Result search(Request request) throws Exception {
        String cacheKey = "Account_" + request.getAccount();
        Result result = new Result();
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            CacheInfo cacheInfo = (CacheInfo) valueWrapper.get();
            result.setAccount(cacheInfo.getAccoutnNumber());
            result.setType(cacheInfo.getType());
            result.setValue(cacheInfo.getValue());
            result.setLastModification(cacheInfo.getLastModification());
        }
        return result;
    }
}

