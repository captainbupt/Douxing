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
    public int commentNumber;

    public Notice() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getCategoryType() {
        return CATEGORY_TYPE;
    }
}
