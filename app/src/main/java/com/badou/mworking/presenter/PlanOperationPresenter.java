package com.badou.mworking.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.EntryOperation;
import com.badou.mworking.entity.category.PlanOperation;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.CategoryIntentFactory;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.PlanOperationView;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by badou1 on 2015/7/30.
 */
public class PlanOperationPresenter  extends ListPresenter<PlanOperation> {

    String mRid;
    CategoryDetail mCategoryDetail;
    PlanOperationView mPlanOperationView;
    Fragment mFragment;

    public PlanOperationPresenter(Context context, Fragment fragment, String rid) {
        super(context);
        this.mFragment = fragment;
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mPlanOperationView = (PlanOperationView) v;
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
        PlanOperation operation = mPlanOperationView.getItem(position);
        operation.setCategoryDetail((CategoryDetail) item);
        mPlanOperationView.setItem(position, operation);
    }

    @Override
    public void toDetailPage(PlanOperation data) {
        mFragment.startActivityForResult(CategoryIntentFactory.getIntent(mContext, data.getCategoryDetail().getFmt(), data.getRid()), REQUEST_DETAIL);
    }

    public void setData(CategoryDetail categoryDetail) {
        if (TextUtils.isEmpty(categoryDetail.getLink_to()))
            return;
        this.mCategoryDetail = categoryDetail;
        new CategoryDetailUseCase(mCategoryDetail.getLink_to()).execute(new BaseSubscriber<CategoryDetail>(mContext) {
            @Override
            public void onResponseSuccess(final CategoryDetail data) {
                mPlanOperationView.addData(new ArrayList<PlanOperation>() {{
                    add(new PlanOperation(mCategoryDetail.getLink_to(), data));
                }});
            }
        });
    }
}

