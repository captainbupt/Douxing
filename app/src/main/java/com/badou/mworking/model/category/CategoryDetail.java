package com.badou.mworking.model.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/2.
 */
public class CategoryDetail {

    public int type;
    public int subtype;
    public String subject;
    public String rid;
    public String tagName;
    public int commentNum;
    public int ratingNum;
    public int ratingTotal;
    public String url;
    public int format;
    public int rating;
    public int sign;
    public String categoryName;

    public Task task;

    public CategoryDetail(Context context, JSONObject jsonObject, int type, String rid, String subject, String categoryName) {
        this(context, jsonObject);
        this.type = type;
        this.rid = rid;
        this.subject = subject;
        if (type == Category.CATEGORY_EXAM) {
            String uid = ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
            this.url = Net.getRunHost(context) + Net.EXAM_ITEM(uid, rid);
        }
        this.categoryName = categoryName;
    }

    public CategoryDetail(Context context, JSONObject jsonObject) {
        this.tagName = jsonObject.optString(ResponseParameters.RESOURCE_TAG_NAME);
        this.commentNum = jsonObject.optInt(ResponseParameters.RESOURCE_COMMENT_NUMBER);
        this.ratingNum = jsonObject.optInt(ResponseParameters.RESOURCE_RATING_NUMBER);
        this.ratingTotal = jsonObject.optInt(ResponseParameters.RESOURCE_RATING_TOTAL);
        this.url = jsonObject.optString(ResponseParameters.RESOURCE_URL);
        this.format = jsonObject.optInt(ResponseParameters.RESOURCE_FORMAT);
        String contentString = jsonObject.optString(ResponseParameters.RESOURCE_CONTENT);
        if (!TextUtils.isEmpty(contentString)) {
            try {
                JSONObject contentObject = new JSONObject(contentString);
                rating = contentObject.optInt(ResponseParameters.RESOURCE_CONTENT_RATING);
                sign = contentObject.optInt(ResponseParameters.RESOURCE_CONTENT_SIGNING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String taskString = jsonObject.optString(ResponseParameters.RESOURCE_TASK);
        if (!TextUtils.isEmpty(taskString)) {
            try {
                task = new Task(new JSONObject(taskString));
                task.read = sign;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CategoryDetail(Context context, Category category) {
        this.type = category.getCategoryType();
        this.rid = category.rid;
        this.subject = category.subject;
        this.tagName = category.getClassificationName(context);
        if (category.getCategoryType() == Category.CATEGORY_EXAM) {
            String uid = ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
            this.url = Net.getRunHost(context) + Net.EXAM_ITEM(uid, category.rid);
        } else {
            this.url = category.url + "&uid=" + ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
        }
        this.format = category.subtype;
        if (category.getCategoryType() == Category.CATEGORY_TASK) {
            task = (Task) category;
        }
        this.categoryName = category.getCategoryName(context);
    }
}
