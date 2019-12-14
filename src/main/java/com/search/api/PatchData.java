package com.search.api;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PatchData implements Serializable {
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @NotNull
    private Long value;
}
