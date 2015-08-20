package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 学习计划
 */
public class PlanDetail implements Serializable {
    @SerializedName("config")
    PlanConfiguration config;
    @SerializedName("now")
    String now;

    transient PlanIndex planIndex;

    public PlanIndex getNow() {
        if (planIndex == null) {
            if (now.equals("done")) { // 若已经学完，设置index大于当前stage列表
                planIndex = new PlanIndex(config.stages.size(), 0);
            } else {
                String[] stageIndexString = now.split(":");
                planIndex = new PlanIndex(Integer.parseInt(stageIndexString[0]) - 1, Integer.parseInt(stageIndexString[1]) - 1);
            }
        }
        return planIndex;
    }

    public void setNow(int stageIndex, int resourceIndex) {
        now = (stageIndex + 1) + ":" + (resourceIndex + 1);
    }

    public int getOffline() {
        return config.offline;
    }

    public long getDeadline() {
        return config.deadline;
    }

    public long getStartline() {
        return config.startline;
    }

    public String getSubject() {
        return config.subject;
    }

    public String getDescription() {
        return config.description;
    }

    public List<PlanStage> getStages() {
        return config.stages;
    }

    public PlanStage getCurrentStage() {
        return getStage(getNow().getStageIndex());
    }

    public PlanStage getStage(int index) {
        if (index < config.stages.size())
            return config.stages.get(index);
        else if (config.stages.size() > 0)
            return config.stages.get(0);
        else
            return null;
    }

    public static class PlanConfiguration implements Serializable {
        @SerializedName("offline")
        int offline;
        @SerializedName("deadline")
        long deadline;
        @SerializedName("startline")
        long startline;
        @SerializedName("subject")
        String subject;
        @SerializedName("desc")
        String description;
        @SerializedName("stages")
        List<PlanStage> stages;
    }

    public static boolean isReadable(PlanIndex currentIndex, int stageIndex, int resourceIndex) {
        if (stageIndex < currentIndex.getStageIndex()) {
            return true;
        } else if (stageIndex == currentIndex.getStageIndex()) {
            return resourceIndex <= currentIndex.getResourceIndex();
        } else {
            return false;
        }
    }

    public static boolean isFinish(PlanIndex currentIndex, int stageIndex, int resourceIndex) {
        if (stageIndex < currentIndex.getStageIndex()) {
            return true;
        } else if (stageIndex == currentIndex.getStageIndex()) {
            return resourceIndex < currentIndex.getResourceIndex();
        } else {
            return false;
        }
    }

}
