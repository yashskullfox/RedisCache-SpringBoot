package com.search.api;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class CacheData implements Serializable {

    private String type;
    @NotBlank
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

