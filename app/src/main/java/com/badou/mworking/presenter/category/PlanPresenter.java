package com.badou.mworking.presenter.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.PlanIntroductionActivity;
import com.badou.mworking.R;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.CommentPresenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.PlanView;

public class PlanPresenter extends CategoryBasePresenter {

    private static final int REQUEST_PLAN_INTRODUCTION = 28413;
    private static final String RESULT_STAGE_INDEX = "stage_index";

    PlanIntroductionPresenter mPlanIntroductionPresenter;
    PlanStagePresenter mPlanStagePresenter;
    CommentPresenter mCommentPresenter;
    PlanView mPlanView;
    int mStageIndex = -1;

    public PlanPresenter(Context context, String rid) {
        super(context, Category.CATEGORY_ENTRY, rid, false);
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
        // 如果刷新后的index不一致，说明前一阶段已完成，可以显示提示
        if (mStageIndex < categoryDetail.getPlan().getNow().getStageIndex() && mStageIndex != -1) {
            mPlanView.showToast(String.format(mContext.getString(R.string.plan_stage_finished), categoryDetail.getPlan().getStage(mStageIndex).getSubject()));
        }
        mStageIndex = categoryDetail.getPlan().getNow().getStageIndex();
        // 如果已经学完，则设置mStageIndex为0
        if (mStageIndex >= categoryDetail.getPlan().getStages().size()) {
            mPlanStagePresenter.setData(categoryDetail, 0);
        } else {
            mPlanStagePresenter.setData(categoryDetail, mStageIndex);
        }
    }

    public void onSettingClicked() {
        if (mCategoryDetail != null)
            ((Activity) mContext).startActivityForResult(PlanIntroductionActivity.getIntent(mContext, mCategoryDetail), REQUEST_PLAN_INTRODUCTION);
    }

    public static Intent getResultForStage(int stageIndex) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_STAGE_INDEX, stageIndex);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLAN_INTRODUCTION && resultCode == Activity.RESULT_OK && data != null) {
            mPlanStagePresenter.setData(mCategoryDetail, data.getIntExtra(RESULT_STAGE_INDEX, -1));
        } else {
            // 这里会接收到打开其他页面的返回信息，如果课程列表显示的是当前阶段，则可能涉及到刷新操作
            if (mPlanStagePresenter.getStageIndex() == mCategoryDetail.getPlan().getNow().getStageIndex())
                getCategoryDetail(mRid);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onBackPressed() {
        return mCommentPresenter.onBackPressed();
    }
}
