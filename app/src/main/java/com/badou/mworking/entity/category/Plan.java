package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

public class Plan extends Category {

    @SerializedName("stage")
    String stage;
    @SerializedName("tm_plan")
    int totalTime;
    @SerializedName("tm_upnow")
    int currentTime;
    @SerializedName("stage_num")
    int stageNumber;

    public String getStage() {
        return stage;
    }

    public int getPercent() {
        return (int) (((float) currentTime / (float) totalTime) * 100);
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getStageNumber() {
        return stageNumber;
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