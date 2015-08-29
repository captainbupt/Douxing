package com.badou.mworking.view.category;



import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.view.BaseView;

public interface PlanIntroductionView extends BaseView {
    void setData(CategoryDetail categoryDetail, int stageIndex);
}
