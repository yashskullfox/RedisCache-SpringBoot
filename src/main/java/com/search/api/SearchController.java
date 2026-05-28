package com.search.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private final Service service;

    public SearchController(Service service) {
        this.service = service;
    }

    @PostMapping(
            value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result search(@RequestBody Request request) throws Exception {
        return service.search(request);
    }
}

