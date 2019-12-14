package com.search.api;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Result implements Serializable {
    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @NotNull private int account;
    private String type;
    @NotNull private Long value;
}
