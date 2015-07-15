package com.badou.mworking.entity.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/2.
 */
public class CategoryDetail {

    public int type;
    public String subject;
    public String rid;
    public String tagName;
    public int commentNum;
    public int ratingNum;
    public int ratingTotal;
    public String url;
    public int format;
    public int rating;
    public boolean isSign;
    public boolean isStore;
    public String categoryName;

    public Task task;

    public CategoryDetail(Context context, JSONObject jsonObject, int type, String rid, String subject, String categoryName) {
        this(context, jsonObject);
        this.type = type;
        this.rid = rid;
        this.subject = subject;
        if (type == Category.CATEGORY_EXAM) {
            String uid = UserInfo.getUserInfo().getUid();
            this.url = Net.getRunHost() + Net.EXAM_ITEM(uid, rid);
        }
        this.categoryName = categoryName;
    }

    public CategoryDetail(Context context, JSONObject jsonObject) {
        System.out.println(jsonObject);
        this.tagName = jsonObject.optString(ResponseParameters.RESOURCE_TAG_NAME);
        this.commentNum = jsonObject.optInt(ResponseParameters.RESOURCE_COMMENT_NUMBER);
        this.ratingNum = jsonObject.optInt(ResponseParameters.RESOURCE_RATING_NUMBER);
        this.ratingTotal = jsonObject.optInt(ResponseParameters.RESOURCE_RATING_TOTAL);
        this.url = jsonObject.optString(ResponseParameters.RESOURCE_URL);
        this.format = jsonObject.optInt(ResponseParameters.RESOURCE_FORMAT);
        this.isStore = jsonObject.optBoolean("store");
        String contentString = jsonObject.optString(ResponseParameters.RESOURCE_CONTENT);
        if (!TextUtils.isEmpty(contentString)) {
            try {
                JSONObject contentObject = new JSONObject(contentString);
                rating = contentObject.optInt(ResponseParameters.RESOURCE_CONTENT_RATING);
                isSign = contentObject.optInt(ResponseParameters.RESOURCE_CONTENT_SIGNING) == Constant.READ_YES;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String taskString = jsonObject.optString(ResponseParameters.RESOURCE_TASK);
        if (!TextUtils.isEmpty(taskString)) {
            try {
                task = new Task(context, new JSONObject(taskString));
                task.setRead(isSign);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CategoryDetail(Context context, Category category) {
        this.type = category.getCategoryType();
        this.rid = category.rid;
        this.subject = category.subject;
        this.tagName = category.getClassificationName();
        if (category.getCategoryType() == Category.CATEGORY_EXAM) {
            String uid = UserInfo.getUserInfo().getUid();
            this.url = Net.getRunHost() + Net.EXAM_ITEM(uid, category.rid);
        } else {
            this.url = category.url + "&uid=" + UserInfo.getUserInfo().getUid();
        }
        this.format = category.subtype;
        if (category.getCategoryType() == Category.CATEGORY_TASK) {
            task = (Task) category;
        }
        this.categoryName = category.getCategoryName(context);
    }
}
