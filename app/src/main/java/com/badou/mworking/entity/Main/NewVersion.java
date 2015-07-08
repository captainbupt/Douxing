package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewVersion {
    @Expose
    @SerializedName("new")
    int isNew;
    @Expose
    String desc;
    @Expose
    String url;

    public boolean hasNewVersion() {
        return isNew == 1;
    }

    public String getDescription() {
        return desc;
    }

    public String getUrl() {
        return url;
    }
}
