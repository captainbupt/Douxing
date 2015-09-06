package com.badou.mworking.entity.user;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.util.SPHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class UserInfo {

    public static final String ANONYMOUS_ACCOUNT = "anonymous";
    public static final String ANONYMOUS_PASSWORD = "anonymous";

    private static UserInfo userInfo;
    private String account;

    @SerializedName("uid")
    String uid;
    @SerializedName("newuser")
    Integer newuser;
    @SerializedName("host")
    String host;
    @SerializedName("desc")
    String desc;
    @SerializedName("tag")
    String tag;
    @SerializedName("admin")
    Integer admin;
    @SerializedName("shuffle")
    Shuffle shuffle;
    @SerializedName("name")
    String name;
    @SerializedName("company")
    String company;
    @SerializedName("access")
    Integer access;
    @SerializedName("lang")
    String lang;
    @SerializedName("hxpwd")
    String hxpwd;
    @SerializedName("credit")
    Credit credit;

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
        SPHelper.setUserInfo(userInfo);
        SPHelper.setUserAccount(account);
    }

    public static void clearUserInfo(AppApplication appApplication) {
        JPushInterface.stopPush(appApplication);
        EMChatEntity.getInstance().logout(null);
        SPHelper.setUserInfo(null);
    }

    public String getHxpwd() {
        return hxpwd;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public static UserInfo getUserInfo() {
        if (userInfo == null) {
            try {
                userInfo = SPHelper.getUserInfo();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

    public boolean isAnonymous() {
        return account.equals(ANONYMOUS_ACCOUNT);
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

    public int getCredit() {
        if (credit == null)
            return 0;
        return credit.dayact;
    }

    public void setCredit(int credit) {
        this.credit.dayact = credit;
    }

    static class Credit {
        @SerializedName("dayact")
        int dayact;
    }

}