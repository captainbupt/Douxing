package com.badou.mworking.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.entity.category.Category;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class MessageCenter {

    public static final String TYPE_NOTICE = "notice";
    public static final String TYPE_TRAINING = "training";
    public static final String TYPE_EXAM = "exam";
    public static final String TYPE_TASK = "task";
    public static final String TYPE_SHELF = "shelf";
    public static final String TYPE_CHATTER = "post";
    public static final String TYPE_ASK = "ask";
    public static final String TYPE_ENTRY = "entry";
    public static final String TYPE_PLAN = "plan";
    public static final String TYPE_SURVEY = "survey";

    transient int id;
    transient long ts;
    @SerializedName("type")
    String type;
    @SerializedName("desc")
    String description;
    @SerializedName("add")
    String add;

    public MessageCenter(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(MTrainingDBHelper.PRIMARY_ID));
        ts = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_TS));
        type = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_TYPE));
        description = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_DESCRIPTION));
        add = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_ADD));
    }

    public ContentValues getContentValue() {
        ContentValues values = new ContentValues();
        values.put(MTrainingDBHelper.MESSAGE_CENTER_TS, ts);
        values.put(MTrainingDBHelper.MESSAGE_CENTER_TYPE, type);
        values.put(MTrainingDBHelper.MESSAGE_CENTER_DESCRIPTION, description);
        values.put(MTrainingDBHelper.MESSAGE_CENTER_ADD, add);
        return values;
    }

    public int getCategoryType() {
        if (type.equals(TYPE_NOTICE))
            return Category.CATEGORY_NOTICE;
        if (type.equals(TYPE_TASK))
            return Category.CATEGORY_TASK;
        if (type.equals(TYPE_TRAINING))
            return Category.CATEGORY_TRAINING;
        if (type.equals(TYPE_EXAM))
            return Category.CATEGORY_EXAM;
        if (type.equals(TYPE_SHELF))
            return Category.CATEGORY_SHELF;
        if (type.equals(TYPE_ENTRY))
            return Category.CATEGORY_ENTRY;
        if (type.equals(TYPE_PLAN))
            return Category.CATEGORY_PLAN;
        if (type.equals(TYPE_SURVEY))
            return Category.CATEGORY_SURVEY;
        return -1;
    }

    public int getId() {
        return id;
    }

    public long getTs() {
        return ts;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getAdd() {
        return add;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
