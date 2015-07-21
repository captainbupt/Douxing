package com.badou.mworking.entity.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryComment extends Comment {

    @Expose
    @SerializedName("employee_id")
    String employee_id;
    @Expose
    @SerializedName("info")
    String info;
    @Expose
    @SerializedName("ts")
    long ts;
    @Expose
    @SerializedName("imgurl")
    String imgurl;
    @Expose
    @SerializedName("whom")
    String whom;

    @Override
    public String getName() {
        return employee_id;
    }

    @Override
    public long getTime() {
        return ts * 1000l;
    }

    @Override
    public String getContent() {
        return info;
    }

    @Override
    public String getWhom() {
        return whom;
    }

    @Override
    public String getImgUrl() {
        return imgurl;
    }
}
