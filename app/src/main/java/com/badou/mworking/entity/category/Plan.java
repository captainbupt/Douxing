package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

public class Plan extends Category {

    @SerializedName("stage")
    String stage;

    public String getStage() {
        return stage;
    }

    @Override
    public int getCategoryType() {
        return CATEGORY_PLAN;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {

    }
}