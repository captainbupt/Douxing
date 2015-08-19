package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.category.CategoryBasePresenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.PlanView;

public class PlanPresenter extends CategoryBasePresenter {

    PlanIntroductionPresenter mPlanIntroductionPresenter;
    PlanStagePresenter mPlanStagePresenter;
    CommentPresenter mCommentPresenter;
    PlanView mPlanView;


    public PlanPresenter(Context context, String rid) {
        super(context, Category.CATEGORY_ENTRY, rid);
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mPlanView = (PlanView) v;
    }

    public void setChildPresenters(PlanIntroductionPresenter planIntroductionPresenter, PlanStagePresenter planOperationPresenter, CommentPresenter commentPresenter) {
        this.mPlanIntroductionPresenter = planIntroductionPresenter;
        this.mPlanStagePresenter = planOperationPresenter;
        this.mCommentPresenter = commentPresenter;
    }

    @Override
    public void setData(CategoryDetail categoryDetail) {
        super.setData(categoryDetail);
        mPlanIntroductionPresenter.setData(categoryDetail);
        mPlanStagePresenter.setData(categoryDetail);
        mPlanView.setSwipeEnable(true);
    }

    @Override
    public boolean onBackPressed() {
        return mCommentPresenter.onBackPressed();
    }
}
