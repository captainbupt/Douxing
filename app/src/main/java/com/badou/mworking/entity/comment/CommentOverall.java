package com.badou.mworking.entity.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentOverall<T> {
    @Expose
    @SerializedName("ttlcnt")
    int ttlcnt;
    @Expose
    @SerializedName("result")
    List<T> result;

    public int getTotalCount() {
        return ttlcnt;
    }

    public List<T> getResult() {
        return result;
    }
}
