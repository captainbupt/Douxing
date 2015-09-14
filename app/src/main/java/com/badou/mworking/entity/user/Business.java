package com.badou.mworking.entity.user;

public class Business {
    int title;
    String account;
    String password;

    public Business(int title, String account, String password) {
        this.title = title;
        this.account = account;
        this.password = password;
    }

    public int getTitle() {
        return title;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
