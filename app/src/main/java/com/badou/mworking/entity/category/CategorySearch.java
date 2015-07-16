package com.badou.mworking.entity.category;

import com.badou.mworking.net.ResponseParameters;

import org.json.JSONObject;

public class CategorySearch {
    public String subject;
    public String rid;
    public long ts;
    public int top;
    public int type;

    public CategorySearch(int type, JSONObject jsonObject) {
        this.type = type;
        subject = jsonObject.optString(ResponseParameters.CATEGORY_SUBJECT);
        rid = jsonObject.optString(ResponseParameters.CATEGORY_RID);
        ts = jsonObject.optLong(ResponseParameters.CATEGORY_TIME) * 1000;
        top = jsonObject.optInt(ResponseParameters.CATEGORY_TOP, 0);
    }
}
