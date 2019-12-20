package com.search.api;

public class Request {

    /* request to search for account from cache and see the stored value in cache */

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    private String account;
}
