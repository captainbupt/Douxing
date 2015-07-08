package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logo {
    @Expose
    @SerializedName("new")
    int isNew;
    @Expose
    String url;

    public boolean hasNewVersion() {
        return isNew == 1;
    }

    public String getUrl() {
        return url;
    }
}
