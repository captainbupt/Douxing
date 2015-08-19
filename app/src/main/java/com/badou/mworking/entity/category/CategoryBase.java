package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

public class CategoryBase {
    @SerializedName("subject")
    String subject;
    @SerializedName("fmt")
    int format;
    @SerializedName("type")
    String type;

    transient String rid;

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public String getSubject() {
        return subject;
    }

    public int getFormat() {
        return format;
    }

    public int getType() {
        if (type.equals("notice")) {
            return Category.CATEGORY_NOTICE;
        } else if (type.equals("training")) {
            return Category.CATEGORY_TRAINING;
        } else if (type.equals("exam")) {
            return Category.CATEGORY_EXAM;
        } else if (type.equals("task")) {
            return Category.CATEGORY_TASK;
        } else if (type.equals("shelf")) {
            return Category.CATEGORY_SHELF;
        } else if (type.equals("entry")) {
            return Category.CATEGORY_ENTRY;
        } else if (type.equals("plan")) {
            return Category.CATEGORY_PLAN;
        }
        return Category.CATEGORY_TRAINING;
    }
}
