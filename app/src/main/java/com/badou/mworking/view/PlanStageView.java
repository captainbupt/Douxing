package com.badou.mworking.view;

import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.PlanIndex;
import com.badou.mworking.entity.category.PlanStage;

public interface PlanStageView extends BaseListView<CategoryBase> {
    void setStageIndex(int index);
    void setCurrentIndex(PlanIndex planIndex);
}


