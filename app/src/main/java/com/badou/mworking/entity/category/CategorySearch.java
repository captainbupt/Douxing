package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class CategorySearch {
    @SerializedName("subject")
    public String subject;
    @SerializedName("rid")
    public String rid;
    @SerializedName("ts")
    public long ts;
    @SerializedName("top")
    public int top;
    @SerializedName("type")
    public int type;

}
