package com.search.api;

import java.util.List;

public interface Service {
    Result search(Request request) throws Exception;
}
