package com.search;

import java.time.Instant;

public class CacheInfo {

    private int accoutnNumber;
    private String type;
    private Long value;
    Instant lastModification;

    public CacheInfo(int accoutnNumber, String type, Long value, Instant lastModification) {
        this.accoutnNumber = accoutnNumber;
        this.type = type;
        this.value = value;
        this.lastModification = lastModification;
    }

    public int getAccoutnNumber() {
        return accoutnNumber;
    }

    public void setAccoutnNumber(int accoutnNumber) {
        this.accoutnNumber = accoutnNumber;
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
