package com.search.api;

import java.util.List;

public interface Service {
    List<Result> search(Request request) throws Exception;
}
