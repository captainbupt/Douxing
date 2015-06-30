package com.badou.mworking.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.badou.mworking.R;
import com.badou.mworking.entity.category.Category;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/25 0025.
 */
public class Store {

    public static final int TYPE_ASK = 5;
    public static final int TYPE_NOTICE = 1;
    public static final int TYPE_TRAINING = 2;
    public static final int TYPE_EXAM = 3;
    public static final int TYPE_TASK = 4;
    public static final int TYPE_SHELF = 9;
    public static final int TYPE_CHATTER = 7;

    public static final String TYPE_STRING_ASK = "ask";
    public static final String TYPE_STRING_NOTICE = "notice";
    public static final String TYPE_STRING_TRAINING = "training";
    public static final String TYPE_STRING_EXAM = "exam";
    public static final String TYPE_STRING_TASK = "task";
    public static final String TYPE_STRING_SHELF = "shelf";
    public static final String TYPE_STRING_CHATTER = "qas";

    public String id;
    public String sid;
    public int type;
    public long ts;
    public String employee_id;
    public String subject;
    public Chatter chatter;

    public Store(Context context, JSONObject jsonObject) {
        id = jsonObject.optString("id");
        sid = jsonObject.optString("sid");
        type = Integer.parseInt(jsonObject.optString("type"));
        ts = Long.parseLong(jsonObject.optString("ts"));
        employee_id = jsonObject.optString("employee_id");
        subject = jsonObject.optString("subject");
        if (TextUtils.isEmpty(subject)) {
            subject = context.getString(R.string.tip_message_center_resource_gone);
        }
        JSONObject chatterJsonObject = jsonObject.optJSONObject("qas");
        if (chatterJsonObject != null)
            chatter = new Chatter(chatterJsonObject);
    }

    public int getIconRes() {
        switch (type) {
            case TYPE_ASK:
                return R.drawable.button_ask;
            case TYPE_CHATTER:
                return R.drawable.icon_user_detail_default_head;
            case TYPE_EXAM:
                return R.drawable.button_exam;
            case TYPE_NOTICE:
                return R.drawable.button_notice;
            case TYPE_SHELF:
                return R.drawable.button_shelf;
            case TYPE_TASK:
                return R.drawable.button_task;
            case TYPE_TRAINING:
                return R.drawable.button_training;
            default:
                return R.drawable.button_notice;
        }
    }

    public String getTypeString() {
        switch (type) {
            case TYPE_ASK:
                return TYPE_STRING_ASK;
            case TYPE_CHATTER:
                return TYPE_STRING_CHATTER;
            case TYPE_EXAM:
                return TYPE_STRING_EXAM;
            case TYPE_NOTICE:
                return TYPE_STRING_NOTICE;
            case TYPE_SHELF:
                return TYPE_STRING_SHELF;
            case TYPE_TASK:
                return TYPE_STRING_TASK;
            case TYPE_TRAINING:
                return TYPE_STRING_TRAINING;
            default:
                return TYPE_STRING_NOTICE;
        }
    }

    public int getCategoryType() {
        switch (type) {
            case TYPE_EXAM:
                return Category.CATEGORY_EXAM;
            case TYPE_NOTICE:
                return Category.CATEGORY_NOTICE;
            case TYPE_SHELF:
                return Category.CATEGORY_SHELF;
            case TYPE_TASK:
                return Category.CATEGORY_TASK;
            case TYPE_TRAINING:
                return Category.CATEGORY_TRAINING;
            default:
                return Category.CATEGORY_NOTICE;
        }
    }
}
