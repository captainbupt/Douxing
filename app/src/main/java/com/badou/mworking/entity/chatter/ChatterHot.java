package com.badou.mworking.entity.chatter;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class ChatterHot {
    @SerializedName("uid")
    String uid;
    @SerializedName("employee_id")
    String name;
    @SerializedName("eid")
    String username;
    @SerializedName("imgurl")
    String headUrl;
    @SerializedName("circle_lv")
    int level;
    @SerializedName("detail")
    Content detail;

    static class Content {
        @SerializedName("p")
        int topicNumber;
        @SerializedName("c")
        int praiseNumber;
        @SerializedName("r")
        int commentNumber;
        @SerializedName("t")
        int total;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public int getLevel() {
        return level;
    }

    public int getTopicNumber() {
        return detail.topicNumber;
    }

    public int getPraiseNumber() {
        return detail.praiseNumber;
    }

    public int getCommentNumber() {
        return detail.commentNumber;
    }

    public int getTotal() {
        return detail.total;
    }
}
