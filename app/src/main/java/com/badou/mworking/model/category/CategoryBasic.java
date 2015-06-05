package com.badou.mworking.model.category;

import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/5.
 */
public class CategoryBasic {
    public String subject;
    public String rid;
    public long ts;
    public int top;
    public int type;

    public CategoryBasic(int type, JSONObject jsonObject) {
        this.type = type;
        subject = jsonObject.optString(ResponseParams.CATEGORY_SUBJECT);
        rid = jsonObject.optString(ResponseParams.CATEGORY_RID);
        ts = jsonObject.optLong(ResponseParams.CATEGORY_TIME) * 1000;
        top = jsonObject.optInt(ResponseParams.CATEGORY_TOP, 0);
    }
}
