package com.badou.mworking.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.net.ResponseParameters;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/15.
 */
public class MessageCenter {

    public static final String TYPE_NOTICE = "notice";
    public static final String TYPE_TRAINING = "training";
    public static final String TYPE_EXAM = "exam";
    public static final String TYPE_TASK = "task";
    public static final String TYPE_SHELF = "shelf";
    public static final String TYPE_CHATTER = "post";
    public static final String TYPE_ASK = "ask";
    public static final String TYPE_CHAT = "chat";

    public int id;
    public long ts;
    public String type;
    public String description;
    public String add;

    public MessageCenter(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(MTrainingDBHelper.PRIMARY_ID));
        ts = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_TS));
        type = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_TYPE));
        description = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_DESCRIPTION));
        add = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.MESSAGE_CENTER_ADD));
    }

    public MessageCenter(Bundle bundle, long time) throws JSONException {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        ts = time;
        JSONObject extraJson = new JSONObject(extras);
        type = extraJson.optString(ResponseParameters.MESSAGE_CENTER_TYPE);
        description = extraJson.optString(ResponseParameters.MESSAGE_CENTER_DESCRIPTION);
        add = extraJson.optString(ResponseParameters.MESSAGE_CENTER_ADD);
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
        return -1;
    }
}
