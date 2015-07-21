package com.badou.mworking.entity.comment;

import com.google.gson.annotations.SerializedName;

public class ChatterComment extends Comment {

    @SerializedName("t")
    long time;
    @SerializedName("e")
    String name;
    @SerializedName("c")
    String content;
    @SerializedName("imgurl")
    String imgUrl;
    @SerializedName("uid")
    String uid;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getTime() {
        return time * 1000;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getWhom() {
        return uid;
    }

    @Override
    public String getImgUrl() {
        return imgUrl;
    }
}
