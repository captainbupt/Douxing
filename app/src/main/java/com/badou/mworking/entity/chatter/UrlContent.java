package com.badou.mworking.entity.chatter;

import com.google.gson.annotations.SerializedName;

public class UrlContent {
    @SerializedName("img")
    String img;
    @SerializedName("txt")
    String description;
    @SerializedName("url")
    String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}