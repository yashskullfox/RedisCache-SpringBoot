package com.search.api;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CacheData implements Serializable {

    /* This class is POST Request or to add Data in Cache Bucket */

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

    private String type;
    @NotNull private String value;
}
