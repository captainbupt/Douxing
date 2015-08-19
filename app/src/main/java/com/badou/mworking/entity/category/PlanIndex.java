package com.badou.mworking.entity.category;

public class PlanIndex {

    int stageIndex;
    int resourceIndex;

    public PlanIndex(int stageIndex, int resourceIndex) {
        this.stageIndex = stageIndex;
        this.resourceIndex = resourceIndex;
    }

    public int getStageIndex() {
        return stageIndex;
    }

    public int getResourceIndex() {
        return resourceIndex;
    }
}
