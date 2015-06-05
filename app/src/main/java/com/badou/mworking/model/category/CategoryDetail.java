package com.badou.mworking.model.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;

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

    public Task task;

    public CategoryDetail(Context context, JSONObject jsonObject, int type, String rid, String subject) {
        this(context, jsonObject);
        this.type = type;
        this.rid = rid;
        this.subject = subject;
        if (type == Category.CATEGORY_EXAM) {
            String uid = ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
            this.url = Net.getRunHost(context) + Net.EXAM_ITEM(uid, rid);
        }
    }

    public CategoryDetail(Context context, JSONObject jsonObject) {
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
                sign = contentObject.optInt(ResponseParams.RESOURCE_CONTENT_SIGNING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String taskString = jsonObject.optString(ResponseParams.RESOURCE_TASK);
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
    }
}
