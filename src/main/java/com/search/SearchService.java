package com.search;

import com.search.api.Request;
import com.search.api.Result;
import com.search.api.Service;
import org.springframework.cache.Cache;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service("SearchService")
public class SearchService implements Service {

    private Cache cache;

    public SearchService(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Result search(Request request) throws Exception {
        String accountNumber = request.getAccount();
        List cacheData = new ArrayList<>();
        Result result = new Result();
        String cacheKey = "Account_" + accountNumber;
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            CacheInfo cacheInfo =
                    (CacheInfo) valueWrapper.get();
            result.setAccount(cacheInfo.getAccoutnNumber());
            result.setType(cacheInfo.getType());
            result.setValue(cacheInfo.getValue());
            result.setLastModification(cacheInfo.getLastModification());
        }
        return result;
    }
}
