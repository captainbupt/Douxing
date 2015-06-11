package com.badou.mworking.model;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/11.
 */
public class ChatterTopic {
    public String key;
    public long time;

    public ChatterTopic(JSONObject jsonObject) {
        key = jsonObject.optString("key");
        time = jsonObject.optLong("value") * 1000;
    }

    public ChatterTopic(String key, long time) {
        this.key = key;
        this.time = time;
    }
}
