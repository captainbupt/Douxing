package com.badou.mworking.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.category.CategoryBaseUseCase;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.EntryOperation;
import com.badou.mworking.entity.category.PlanDetail;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.factory.CategoryIntentFactory;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.PlanStageView;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class PlanStagePresenter extends ListPresenter<CategoryBase> {

    String mRid;
    CategoryDetail mCategoryDetail;
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
    public void toDetailPage(CategoryBase data) {
        mFragment.startActivityForResult(CategoryIntentFactory.getIntent(mContext, data.getType(), data.getRid()), REQUEST_DETAIL);
    }

    public void setData(CategoryDetail categoryDetail) {
        this.mCategoryDetail = categoryDetail;
        final PlanDetail planDetail = mCategoryDetail.getPlan();
        System.out.println("plan detail: " + GsonUtil.toJson(planDetail));
        new CategoryBaseUseCase(planDetail.getStages().get(planDetail.getNow().getStageIndex() - 1).getLink()).execute(new BaseSubscriber<List<CategoryBase>>(mContext) {
            @Override
            public void onResponseSuccess(List<CategoryBase> data) {
                mPlanStageView.setStageIndex(planDetail.getNow().getStageIndex());
                mPlanStageView.setCurrentIndex(planDetail.getNow());
                mPlanStageView.setData(data);
            }
        });
    }
}

