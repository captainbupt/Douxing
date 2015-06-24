package com.badou.mworking.entity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/12.
 */
public class ChatterHot {
    public String uid;
    public String name;
    public String username;
    public String headUrl;
    public int level;
    public int topicNumber;
    public int praiseNumber;
    public int commentNumber;
    public int total;

    public ChatterHot(JSONObject jsonObject) {
        uid = jsonObject.optString("uid");
        name = jsonObject.optString("employee_id");
        username = jsonObject.optString("eid");
        headUrl = jsonObject.optString("imgurl");
        level = jsonObject.optInt("circle_lv");
        JSONObject detailObject = jsonObject.optJSONObject("detail");
        topicNumber = detailObject.optInt("p");
        praiseNumber = detailObject.optInt("c");
        commentNumber = detailObject.optInt("r");
        total = detailObject.optInt("t");
    }
}
