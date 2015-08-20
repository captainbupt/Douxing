package com.badou.mworking.presenter.category;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.badou.mworking.R;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.category.CategoryBaseUseCase;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.EntryOperation;
import com.badou.mworking.entity.category.PlanDetail;
import com.badou.mworking.entity.category.PlanStage;
import com.badou.mworking.factory.CategoryIntentFactory;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.PlanStageView;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class PlanStagePresenter extends ListPresenter<CategoryBase> {

    String mRid;
    CategoryDetail mCategoryDetail;
    int mStageIndex;
    PlanStage mPlanStage;
    PlanStageView mPlanStageView;
    Fragment mFragment;

    public PlanStagePresenter(Context context, Fragment fragment, String rid) {
        super(context);
        this.mFragment = fragment;
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mPlanStageView = (PlanStageView) v;
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<EntryOperation>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        return null;
    }

    @Override
    public void onResponseItem(int position, Serializable item) {

    }

    @Override
    public void onItemClick(CategoryBase data, int position) {
        super.onItemClick(data, position);
        if (PlanDetail.isReadable(mCategoryDetail.getPlan().getNow(), mStageIndex, position)) {
            mFragment.startActivityForResult(CategoryIntentFactory.getIntentWithoutComment(mContext, data.getType(), data.getRid()), REQUEST_DETAIL);
        } else {
            mPlanStageView.showToast(R.string.plan_resource_unreadable);
        }
    }

    @Override
    public void toDetailPage(CategoryBase data) {
    }

    public void setData(CategoryDetail categoryDetail, final int stageIndex) {
        this.mCategoryDetail = categoryDetail;
        this.mPlanStage = mCategoryDetail.getPlan().getStage(stageIndex);
        this.mStageIndex = stageIndex;
        if (mPlanStage == null) {
            mPlanStageView.showNoneResult();
            return;
        }
        new CategoryBaseUseCase(mPlanStage.getLink()).execute(new BaseSubscriber<List<CategoryBase>>(mContext) {
            @Override
            public void onResponseSuccess(List<CategoryBase> data) {
                mPlanStageView.setStageIndex(stageIndex);
                mPlanStageView.setCurrentIndex(mCategoryDetail.getPlan().getNow());
                mPlanStageView.setData(data);
            }
        });
    }

    public int getStageIndex(){
        return mStageIndex;
    }
}

