package com.badou.mworking.entity.category;

import com.badou.mworking.entity.category.Category;
import android.content.Context;

import com.badou.mworking.database.MTrainingDBHelper;

import org.json.JSONObject;

/**
 * 功能描述: 通知实体类
 */
public class Notice extends Category {

    public final int CATEGORY_TYPE = CATEGORY_NOTICE;
    public final String CATEGORY_KEY_NAME = CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public final String CATEGORY_KEY_UNREAD_NUM = CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//通知 的 未读数量
    public int commentNumber;

    public Notice() {
    }

    /**
     * 功能描述: 通知公告json解析
     *
     * @param jsonObject
     */
    public Notice(Context context, JSONObject jsonObject) {
        super(context, jsonObject);
    }

    @Override
    public int getCategoryType() {
        return CATEGORY_TYPE;
    }

    @Override
    public String getCategoryKeyName() {
        return CATEGORY_KEY_NAME;
    }

    @Override
    public String getCategoryKeyUnread() {
        return CATEGORY_KEY_UNREAD_NUM;
    }
}
