package com.badou.mworking.entity.user;

import com.badou.mworking.entity.Chatter;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/12.
 */
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
        this.name = chatter.name;
        this.department = chatter.department;
        this.headUrl = chatter.headUrl;
        this.level = chatter.level;
    }

    public UserChatterInfo(JSONObject jsonObject) {
        this.department = jsonObject.optString("dpt");
        this.level = jsonObject.optInt("circle_lv");
        this.name = jsonObject.optString("name");
        this.headUrl = jsonObject.optString("imgurl");
    }
}
