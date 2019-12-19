package com.search.api;

public interface DataService {

    void addDataToCache(int accountNumber, CacheData cacheData) throws Exception;

    void removeDataFromCache(int accountNumber) throws Exception;

    void updateDataInCache(int accountNumber, PatchData patchData) throws Exception;
}
