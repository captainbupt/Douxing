package com.badou.mworking.entity.category;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CategoryOverall<T> {
    @Expose
    int ttlcnt;
    @Expose
    int newcnt;
    @Expose
    List<T> list;

    public int getTotalCount() {
        return ttlcnt;
    }

    public int getUnreadCount() {
        return newcnt;
    }

    public List<T> getCategoryList() {
        return list;
    }
}
