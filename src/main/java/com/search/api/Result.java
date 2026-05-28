package com.search.api;

import java.io.Serializable;
import java.time.Instant;

public class Result implements Serializable {

    private int account;
    private String type;
    private Long value;
    private Instant lastModification;

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

    public Instant getLastModification() {
        return lastModification;
    }

    public void setLastModification(Instant lastModification) {
        this.lastModification = lastModification;
    }
}

