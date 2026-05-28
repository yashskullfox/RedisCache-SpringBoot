package com.search.api;

import java.io.Serializable;

public class Request implements Serializable {

    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

