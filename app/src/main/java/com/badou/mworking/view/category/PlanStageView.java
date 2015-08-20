package com.badou.mworking.view.category;

import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.PlanIndex;
import com.badou.mworking.entity.category.PlanStage;
import com.badou.mworking.view.BaseListView;

public interface PlanStageView extends BaseListView<CategoryBase> {
    void setStageIndex(int index);
    void setCurrentIndex(PlanIndex planIndex);
}


