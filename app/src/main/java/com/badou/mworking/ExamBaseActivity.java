package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.PlanInfo;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.net.Net;
import com.badou.mworking.presenter.category.CategoryBasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamBaseActivity extends CategoryBaseActivity {

    @Bind(R.id.content_container)
    FrameLayout mContentContainer;

    public static Intent getIntent(Context context, String rid, String planTitle) {
        return CategoryBaseActivity.getIntent(context, ExamBaseActivity.class, rid, planTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_exam);
        if (mPlanInfo == null) {
            setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_EXAM));
        }
        ButterKnife.bind(this);
        mPresenter.attachView(this);
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        return new CategoryBasePresenter(mContext, Category.CATEGORY_EXAM, mReceivedIntent.getStringExtra(KEY_RID), mPlanInfo);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, PlanInfo planInfo) {
        super.setData(rid, categoryDetail, planInfo);
        WebViewFragment mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(Net.getRunHost() + Net.EXAM_ITEM(UserInfo.getUserInfo().getUid(), rid));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }

    @Override
    public void setCommentNumber(int number) {
    }

    @Override
    public void setRatingNumber(int number) {

    }

    @Override
    public void setRated(boolean rated) {

    }

    @Override
    public void showTimingView() {

    }

    @Override
    public void setMaxPeriod(int minute) {

    }

    @Override
    public void setCurrentPeriod(int currentSecond) {

    }
}
