package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.fragment.PDFViewFragment;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.presenter.category.CategoryBasePresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoticeBaseActivity extends CategoryBaseActivity {

    @Bind(R.id.content_container)
    FrameLayout mContentContainer;
    @Bind(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;

    public static Intent getIntent(Context context, String rid, String planTitle) {
        return CategoryBaseActivity.getIntent(context, NoticeBaseActivity.class, rid, planTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_notice);
        ButterKnife.bind(this);
        setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_NOTICE));
        initListener();
        mPresenter.attachView(this);
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        return new CategoryBasePresenter(mContext, Category.CATEGORY_NOTICE, mReceivedIntent.getStringExtra(KEY_RID), mReceivedIntent.getStringExtra(KEY_PLAN_TITLE));
    }

    private void initListener() {
        mBottomView.setCommentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onCommentClicked();
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, boolean isPlan) {
        super.setData(rid, categoryDetail, isPlan);
        if (categoryDetail.getFmt() == Constant.MWKG_FORAMT_TYPE_PDF) {
            // 判断api,太小用web
            if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
                // pdf文件已存在 调用
                showPdf(rid, categoryDetail.getUrl());
            } else {// web
                showWeb(Constant.TRAIN_IMG_SHOW + rid + Constant.TRAIN_IMG_FORMAT);
            }
        } else if (categoryDetail.getFmt() == Constant.MWKG_FORAMT_TYPE_HTML) {
            showWeb(categoryDetail.getUrl());
        } else {
            showToast(R.string.category_unsupport_type);
            finish();
        }
    }

    public void showPdf(String rid, String url) {
        PDFViewFragment pdfViewFragment = new PDFViewFragment();
        pdfViewFragment.setArguments(PDFViewFragment.getArgument(rid, url));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, pdfViewFragment);
        transaction.commit();
    }

    public void showWeb(String url) {
        WebViewFragment mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(url);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setCommentNumber(int number) {
        mBottomView.setCommentData(number);
    }

    @Override
    public void setRatingNumber(int number) {

    }

    @Override
    public void hideCommentView() {
        mBottomView.setVisibility(View.GONE);
    }
}
