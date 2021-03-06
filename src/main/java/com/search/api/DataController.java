package com.search.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DataController {
    @Autowired private DataService dataService;

    @PostMapping(value = "/Account/{accountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDataToCache(@Valid @RequestBody CacheData cacheData, @PathVariable("accountNumber") int accountNumber) throws Exception {
        dataService.addDataToCache(accountNumber, cacheData);
        return ResponseEntity.ok("Account " + accountNumber +" Added into Data" );
    }

    @PatchMapping(value = "/Account/{accountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDataInCache(@PathVariable int accountNumber, @Valid @RequestBody PatchData patchData) throws Exception{
        dataService.updateDataInCache(accountNumber, patchData);
        return ResponseEntity.ok("Account "+accountNumber+" is updated");
    }

    @DeleteMapping(value = "/Account/{accountNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeDataFromCache(@PathVariable("accountNumber") int accountNumber) throws Exception{
        dataService.removeDataFromCache(accountNumber);
        return ResponseEntity.ok("Removed Account " + accountNumber);
    }

}
