package com.badou.mworking.entity.comment;

import com.google.gson.annotations.Expose;

public class CategoryComment extends Comment {

    @Expose
    String employee_id;
    @Expose
    String info;
    @Expose
    long ts;
    @Expose
    String imgurl;
    @Expose
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
