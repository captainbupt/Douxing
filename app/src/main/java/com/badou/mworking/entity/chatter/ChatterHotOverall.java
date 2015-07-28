package com.badou.mworking.entity.chatter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatterHotOverall {
    @SerializedName("ttl")
    int totalCount;
    @SerializedName("list")
    List<ChatterHot> hotList;

    public List<ChatterHot> getHotList() {
        return hotList;
    }
}
