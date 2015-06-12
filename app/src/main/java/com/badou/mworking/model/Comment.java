package com.badou.mworking.model;

import com.badou.mworking.net.ResponseParameters;

import org.json.JSONObject;

public class Comment {

    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_CHATTER = 1;

    public String name;
    public long time;
    public String content;
    public String whom;
    public String imgUrl;

    public Comment(JSONObject jsonObject, int type) {
        if (type == TYPE_COMMENT) {
            this.name = jsonObject.optString(ResponseParameters.COMMENT_USERNAME);
            this.time = jsonObject.optLong(ResponseParameters.COMMENT_TIME) * 1000;
            this.content = jsonObject.optString(ResponseParameters.COMMENT_CONTENT);
            this.imgUrl = jsonObject.optString(ResponseParameters.COMMENT_HEAD);
            this.whom = jsonObject.optString(ResponseParameters.COMMENT_WHOM);
        } else {
            this.name = jsonObject.optString(ResponseParameters.CHATTER_REPLY_USERNAME);
            this.time = jsonObject.optLong(ResponseParameters.CHATTER_REPLY_TIME) * 1000;
            this.content = jsonObject.optString(ResponseParameters.CHATTER_REPLY_CONTENT);
            this.imgUrl = jsonObject.optString(ResponseParameters.CHATTER_REPLY_HEAD);
            this.whom = jsonObject.optString(ResponseParameters.CHATTER_REPLY_WHOM);
        }
    }
}
