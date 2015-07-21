package com.badou.mworking.entity;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.entity.category.Category;

import org.json.JSONObject;

public class Store {

    public static final int TYPE_ASK = 5;
    public static final int TYPE_NOTICE = 1;
    public static final int TYPE_TRAINING = 2;
    public static final int TYPE_EXAM = 3;
    public static final int TYPE_TASK = 4;
    public static final int TYPE_SHELF = 9;
    public static final int TYPE_CHATTER = 7;
    public static final int TYPE_ENTRY = 10;

    public static final String TYPE_STRING_ASK = "ask";
    public static final String TYPE_STRING_NOTICE = "notice";
    public static final String TYPE_STRING_TRAINING = "training";
    public static final String TYPE_STRING_EXAM = "exam";
    public static final String TYPE_STRING_TASK = "task";
    public static final String TYPE_STRING_SHELF = "shelf";
    public static final String TYPE_STRING_CHATTER = "qas";
    public static final String TYPE_STRING_ENTRY = "entry";

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

    public static int getIconRes(int type) {
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
            case TYPE_ENTRY:
                return R.drawable.button_entry;
            default:
                return R.drawable.button_notice;
        }
    }

    public static String getTypeString(int type) {
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
            case TYPE_ENTRY:
                return TYPE_STRING_ENTRY;
            default:
                return TYPE_STRING_NOTICE;
        }
    }

    public static String getStoreStringFromCategory(int category) {
        return getTypeString(getStoreTypeFromCategory(category));
    }

    public static int getStoreTypeFromCategory(int category) {
        switch (category) {
            case Category.CATEGORY_NOTICE:
                return TYPE_NOTICE;
            case Category.CATEGORY_TRAINING:
                return TYPE_TRAINING;
            case Category.CATEGORY_EXAM:
                return TYPE_EXAM;
            case Category.CATEGORY_TASK:
                return TYPE_TASK;
            case Category.CATEGORY_SHELF:
                return TYPE_SHELF;
            case Category.CATEGORY_ENTRY:
                return TYPE_ENTRY;
            default:
                return Category.CATEGORY_NOTICE;
        }
    }

    public static int getCategoryTypeFromStore(int type) {
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
            case TYPE_ENTRY:
                return Category.CATEGORY_ENTRY;
            default:
                return Category.CATEGORY_NOTICE;
        }
    }
}
