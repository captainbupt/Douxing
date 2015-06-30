package com.badou.mworking.entity.user;

import android.content.Context;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.util.SPUtil;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class UserInfo {

    public static final String ANONYMOUS_ACCOUNT = "anonymous";
    public static final String ANONYMOUS_PASSWORD = "anonymous";

    private static UserInfo userInfo;
    private String account;

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

    public static void setUserInfo(AppApplication appApplication, String account, UserInfo userInfo) {
        UserInfo.userInfo = userInfo;
        userInfo.setAccount(account);
        Set<String> tags = new HashSet<>();
        tags.add(userInfo.tag);
        if (JPushInterface.isPushStopped(appApplication))
            JPushInterface.resumePush(appApplication);
        JPushInterface.setTags(appApplication, tags, null);
        JPushInterface.setAlias(appApplication, userInfo.getUid(), null);
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(userInfo.getAccount());
        // en为英文版，取值zh为中文版。
        AppApplication.changeAppLanguage(appApplication.getResources(), userInfo.getLang());
        SPUtil.setUserInfo(userInfo);
        SPUtil.setUserAccount(account);
    }

    public static void clearUserInfo(AppApplication appApplication) {
        JPushInterface.stopPush(appApplication);
        SPUtil.setUserInfo(null);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public Integer getNewuser() {
        return newuser;
    }

    public String getHost() {
        return host;
    }

    public String getDesc() {
        return desc;
    }

    public String getTag() {
        return tag;
    }

    public boolean isAdmin() {
        return admin == 1;
    }

    public Integer getAdmin() {
        return admin;
    }

    public Shuffle getShuffle() {
        return shuffle;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public Integer getAccess() {
        return access;
    }

    public String getLang() {
        return lang;
    }

}