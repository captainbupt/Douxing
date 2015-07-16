package com.badou.mworking.entity.comment;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CommentOverall<T> {
    @Expose
    int ttlcnt;
    @Expose
    List<T> result;

    public int getTotalCount() {
        return ttlcnt;
    }

    public List<T> getResult() {
        return result;
    }
}
