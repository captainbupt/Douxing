package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logo {
    @SerializedName("new")
    int isNew;
    @SerializedName("url")
    String url;
    @SerializedName("md5")
    String md5;
    @SerializedName("content")
    String content;

    public boolean hasNewVersion() {
        return isNew == 1;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    public String getContent() {
        return content;
    }
}
