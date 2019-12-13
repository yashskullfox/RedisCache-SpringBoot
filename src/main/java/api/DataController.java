package api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DataController {
    private DataService dataService;

    @PutMapping(value = "/Account/{AccountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void addDataToCache(@PathVariable int accountNumber, @Valid @RequestBody CacheData cacheData) throws Exception {
        dataService.addDataToCache(accountNumber, cacheData);
        return;
    }

    @PatchMapping(value = "/Account/{AccountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void addDataToCache(@PathVariable int accountNumber, @Valid @RequestBody PatchData patchData) throws Exception{
        dataService.updateDataInCache(accountNumber, patchData);
        return;
    }

    @DeleteMapping(value = "/Account/{AccountNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void addDataToCache(@PathVariable int accountNumber) throws Exception{
        dataService.removeDataFromCache(accountNumber);
        return;
    }

}
