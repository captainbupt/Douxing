package com.badou.mworking.entity.category;

import com.badou.mworking.net.ResponseParameters;
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

    public CategorySearch(int type, JSONObject jsonObject) {
        this.type = type;
        subject = jsonObject.optString(ResponseParameters.CATEGORY_SUBJECT);
        rid = jsonObject.optString(ResponseParameters.CATEGORY_RID);
        ts = jsonObject.optLong(ResponseParameters.CATEGORY_TIME) * 1000;
        top = jsonObject.optInt(ResponseParameters.CATEGORY_TOP, 0);
    }
}
