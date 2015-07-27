package com.badou.mworking.entity.user;

import com.badou.mworking.entity.chatter.Chatter;

import org.json.JSONObject;

import java.io.Serializable;

public class UserChatterInfo implements Serializable {
    public String name;//员工号 (登录号? 用户名)
    public String department;
    public String headUrl;//头像地址
    public int level;

    public UserChatterInfo(UserDetail userDetail) {
        name = userDetail.name;
        department = userDetail.dpt;
        headUrl = userDetail.headimg;
        level = userDetail.circle_lv;
    }

    public UserChatterInfo(Chatter chatter) {
        this.name = chatter.getName();
        this.department = chatter.getDepartment();
        this.headUrl = chatter.getHeadUrl();
        this.level = chatter.getLevel();
    }

    public UserChatterInfo(JSONObject jsonObject) {
        this.department = jsonObject.optString("dpt");
        this.level = jsonObject.optInt("circle_lv");
        this.name = jsonObject.optString("name");
        this.headUrl = jsonObject.optString("imgurl");
    }
}
