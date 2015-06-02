package com.badou.mworking.model.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/2.
 */
public class CategoryDetail {

    public String tagName;
    public int commentNum;
    public int ratingNum;
    public int ratingTotal;
    public String url;
    public int format;
    public int rating;
    public boolean sign;

    public Task task;

    public CategoryDetail(JSONObject jsonObject) {
        this.tagName = jsonObject.optString(ResponseParams.RESOURCE_TAG_NAME);
        this.commentNum = jsonObject.optInt(ResponseParams.RESOURCE_COMMENT_NUMBER);
        this.ratingNum = jsonObject.optInt(ResponseParams.RESOURCE_RATING_NUMBER);
        this.ratingTotal = jsonObject.optInt(ResponseParams.RESOURCE_RATING_TOTAL);
        this.url = jsonObject.optString(ResponseParams.RESOURCE_URL);
        this.format = jsonObject.optInt(ResponseParams.RESOURCE_FORMAT);
        String contentString = jsonObject.optString(ResponseParams.RESOURCE_CONTENT);
        if (!TextUtils.isEmpty(contentString)) {
            try {
                JSONObject contentObject = new JSONObject(contentString);
                rating = contentObject.optInt(ResponseParams.RESOURCE_CONTENT_RATING);
                sign = contentObject.optInt(ResponseParams.RESOURCE_CONTENT_SIGNING) == Constant.FINISH_YES;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String taskString = jsonObject.optString(ResponseParams.RESOURCE_TASK);
        if (!TextUtils.isEmpty(taskString)) {
            try {
                task = new Task(new JSONObject(taskString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
