package com.search.api;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PatchData implements Serializable {

    /* This class is Patch Request Data to store in Cache Bucket */

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private Long value;

    @NotNull
    private String action;
}
