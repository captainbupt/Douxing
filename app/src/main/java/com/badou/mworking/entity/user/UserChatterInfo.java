package com.badou.mworking.entity.user;

import com.badou.mworking.entity.chatter.Chatter;

import org.json.JSONObject;

import java.io.Serializable;

public class UserChatterInfo implements Serializable {
    String name;//员工号 (登录号? 用户名)
    String department;
    String headUrl;//头像地址
    int level;
    String uid;

    public UserChatterInfo(String uid, UserDetail userDetail) {
        this.uid = uid;
        name = userDetail.name;
        department = userDetail.dpt;
        headUrl = userDetail.headimg;
        level = userDetail.circle_lv;
    }

    public UserChatterInfo(Chatter chatter) {
        this.uid = chatter.getUid();
        this.name = chatter.getName();
        this.department = chatter.getDepartment();
        this.headUrl = chatter.getHeadUrl();
        this.level = chatter.getLevel();
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public int getLevel() {
        return level;
    }

    public String getUid() {
        return uid;
    }
}
