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
            String[] stageIndexString = now.split(":");
            planIndex = new PlanIndex(Integer.parseInt(stageIndexString[0]) - 1, Integer.parseInt(stageIndexString[0]) - 1);
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

}
