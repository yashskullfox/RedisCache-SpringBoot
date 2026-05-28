package com.search.api;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class PatchData implements Serializable {

    private Long value;
    @NotBlank
    private String action;

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
}

