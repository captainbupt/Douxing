package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.presenter.category.CategoryBasePresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SurveyBaseActivity extends CategoryBaseActivity {

    @Bind(R.id.content_container)
    FrameLayout mContentContainer;


    public static Intent getIntent(Context context, String rid, String planTitle) {
        return CategoryBaseActivity.getIntent(context, SurveyBaseActivity.class, rid, planTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_exam);
        ButterKnife.bind(this);
        setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_SURVEY));
        mPresenter.attachView(this);
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        return new CategoryBasePresenter(mContext, Category.CATEGORY_SURVEY, mReceivedIntent.getStringExtra(KEY_RID), mReceivedIntent.getStringExtra(KEY_PLAN_TITLE));
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, boolean isPlan) {
        super.setData(rid, categoryDetail, isPlan);
        WebViewFragment mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(categoryDetail.getUrl());
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
    public void hideCommentView() {

    }
}
