package com.badou.mworking.entity.comment;

import com.badou.mworking.net.ResponseParameters;

import org.json.JSONObject;

public abstract class Comment {

    public abstract String getName();
    public abstract long getTime();
    public abstract String getContent();
    public abstract String getWhom();
    public abstract String getImgUrl();
}
