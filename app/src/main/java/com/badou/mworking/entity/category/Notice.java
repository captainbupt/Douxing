package com.badou.mworking.entity.category;

import com.badou.mworking.entity.category.Category;

import android.content.Context;

import com.badou.mworking.database.MTrainingDBHelper;
import com.google.gson.annotations.Expose;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 功能描述: 通知实体类
 */
public class Notice extends Category {

    transient int commentNumber = 0;

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    @Override
    public int getCategoryType() {
        return Category.CATEGORY_NOTICE;
    }

    public Notice() {
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
    }
}
