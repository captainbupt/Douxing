package com.badou.mworking.presenter.category;

import android.content.Context;

import com.badou.mworking.domain.category.EnrollUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.PlanIntroductionView;

public class PlanIntroductionPresenter  extends Presenter {

    PlanIntroductionView mPlanIntroductionView;
    CategoryDetail mCategoryDetail;
    String mRid;

    public PlanIntroductionPresenter(Context context, String rid) {
        super(context);
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        mPlanIntroductionView = (PlanIntroductionView) v;
    }

    public void setData(CategoryDetail categoryDetail, int stageIndex) {
        mPlanIntroductionView.setData(categoryDetail, stageIndex);
        this.mCategoryDetail = categoryDetail;
    }
}
