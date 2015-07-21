package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

public class Entry extends Category {
    @SerializedName("maxusr")
    int maxusr;
    @SerializedName("deadline_c")
    long deadline_c;
    @SerializedName("startline_c")
    long startline_c;
    @SerializedName("enroll")
    int enroll;
    @SerializedName("in")
    int in;
    @SerializedName("description")
    String description;

    @Override
    public int getCategoryType() {
        return Category.CATEGORY_ENTRY;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
        this.read = categoryDetail.entry.in;
    }

    public int getRead() {
        return read;
    }
}