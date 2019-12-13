package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RestController
public class SearchController {

    @Autowired private Service service;

    public static final String APPLICATION_JSON_VALUE = "application/json";

    @PostMapping(
            value = "/",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE
    )
    public List<Result> search(@RequestBody Request request) throws Exception{
        return service.search(request);
    }
}
