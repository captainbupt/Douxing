package com.badou.mworking.entity.category;

import android.text.TextUtils;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 功能描述: 考试实体类
 */
public class Exam extends Category {

    @Expose
    @SerializedName("pass")
    int pass;
    @SerializedName("content")
    @Expose
    String contentStr;

    transient Content content;

    public Exam() {
    }

    public boolean isGraded() {
        return getContent().d == 1;
    }

    public int getScore() {
        return getContent().s;
    }

    @Override
    public String getUrl() {
        return Net.getRunHost() + Net.EXAM_ITEM(UserInfo.getUserInfo().getUid(), rid);
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
    }

    private Content getContent() {
        if (content == null) {
            if (!TextUtils.isEmpty(contentStr)) {
                System.out.println(contentStr);
                content = GsonUtil.fromJson(contentStr, Content.class);
            } else {
                content = new Content(0, 0, 0);
            }
        }
        return content;
    }

    @Override
    public int getCategoryType() {
        return Category.CATEGORY_EXAM;
    }

    static class Content {
        @Expose
        @SerializedName("s")
        int s;
        @Expose
        @SerializedName("t")
        int t;
        @Expose
        @SerializedName("d")
        int d;

        public Content() {
        }

        public Content(int s, int t, int d) {
            this.s = s;
            this.t = t;
            this.d = d;
        }

    }

}
