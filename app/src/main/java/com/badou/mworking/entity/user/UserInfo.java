package com.badou.mworking.entity.user;

import android.content.Context;

import com.badou.mworking.entity.Main.Shuffle;
import com.google.gson.annotations.Expose;

public class UserInfo {

    private static UserInfo userInfo;

    // 应该要传入ApplicationContext
    private Context applicationContext;

    @Expose
    private String uid;

    @Expose
    private Integer newuser;
    @Expose
    private String host;
    @Expose
    private String desc;
    @Expose
    private String tag;
    @Expose
    private Integer admin;
    @Expose
    private Shuffle shuffle;
    @Expose
    private String name;
    @Expose
    private String company;
    @Expose
    private Integer access;
    @Expose
    private String lang;

    public static void setUserInfo(UserInfo userInfo) {
        UserInfo.userInfo = userInfo;
    }

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public void initialize(Context applicationContext){
        this.applicationContext = applicationContext;
        shuffle.initialize(applicationContext);
    }
}