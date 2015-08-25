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
        int stageIndex = categoryDetail.getPlan().getNow().getStageIndex();
        if (stageIndex >= categoryDetail.getPlan().getStages().size()) {
            stage = "全部完成";
        } else {
            stage = categoryDetail.getPlan().getStage(stageIndex).getSubject();
        }
    }
}