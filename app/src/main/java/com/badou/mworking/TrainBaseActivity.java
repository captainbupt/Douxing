package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.PlanInfo;
import com.badou.mworking.fragment.PDFViewFragment;
import com.badou.mworking.fragment.TrainMusicFragment;
import com.badou.mworking.fragment.TrainVideoFragment;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.presenter.DownloadPresenter;
import com.badou.mworking.presenter.category.CategoryBasePresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.widget.BottomRatingAndCommentView;
import com.badou.mworking.widget.BottomTimingView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrainBaseActivity extends CategoryBaseActivity {

    private static final String KEY_TRAINING = "training";

    @Bind(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;
    @Bind(R.id.content_container)
    FrameLayout mContentContainer;
    @Bind(R.id.bottom_timing_view)
    BottomTimingView mBottomTimingView;

    private Bundle mSavedInstanceState;
    private boolean isTraining = true;

    public static Intent getIntent(Context context, String rid, boolean isTraining, PlanInfo planInfo) {
        Intent intent = CategoryBaseActivity.getIntent(context, TrainBaseActivity.class, rid, planInfo);
        intent.putExtra(KEY_TRAINING, isTraining);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_training);
        mSavedInstanceState = savedInstanceState;
        ButterKnife.bind(this);
        initView();
        mPresenter.attachView(this);
    }

    private void initView() {
        isTraining = mReceivedIntent.getBooleanExtra(KEY_TRAINING, true);
        if (mPlanInfo == null) {
            if (isTraining) {
                setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_TRAINING));
            } else {
                setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_SHELF));
            }
        }
        mBottomView.setCommentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onCommentClicked();
            }
        });
        mBottomView.setRatingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onRatingClicked();
            }
        });
        mBottomView.setShareClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onShareClicked();
            }
        });
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        return new CategoryBasePresenter(mContext, isTraining ? Category.CATEGORY_TRAINING : Category.CATEGORY_SHELF, mReceivedIntent.getStringExtra(KEY_RID), mPlanInfo);
    }


    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, PlanInfo planInfo) {
        super.setData(rid, categoryDetail, planInfo);
        if (mSavedInstanceState == null) { // 旋转情况下，android自动回保存fragment实例，不必重新添加
            if (categoryDetail.getFmt() == Constant.MWKG_FORAMT_TYPE_PDF) {
                // 判断api,太小用web
                if (Build.VERSION.SDK_INT >= 11) {// pdf
                    // pdf文件已存在 调用
                    showPdf(rid, categoryDetail.getUrl());
                } else {// web
                    showWeb(Constant.TRAIN_IMG_SHOW + rid + Constant.TRAIN_IMG_FORMAT);
                }
            } else if (categoryDetail.getFmt() == Constant.MWKG_FORAMT_TYPE_HTML) {
                showWeb(categoryDetail.getUrl());
            } else if (Constant.MWKG_FORAMT_TYPE_MPEG == categoryDetail.getFmt()) { // 返回MP4格式
                showVideo(rid, categoryDetail.getUrl(), categoryDetail.getSubject());
            } else if (Constant.MWKG_FORAMT_TYPE_MP3 == categoryDetail.getFmt()) { // 返回MP3格式
                showMusic(rid, categoryDetail.getUrl(), categoryDetail.getSubject());
            } else {
                showToast(R.string.category_unsupport_type);
                finish();
            }
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

    public void showMusic(String rid, String url, String subject) {
        final TrainMusicFragment trainMusicFragment = TrainMusicFragment.getFragment(rid, url, subject);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, trainMusicFragment);
        transaction.commit();
        if (mPlanInfo != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    trainMusicFragment.setOnStatusChangedListener(mOnStatusChangedListener);
                }
            }, 200);
        }
    }

    public void showVideo(String rid, String url, String subject) {
        final TrainVideoFragment trainVideoFragment = TrainVideoFragment.getFragment(rid, url, subject);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, trainVideoFragment);
        transaction.commit();
        if (mPlanInfo != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    trainVideoFragment.setOnStatusChangedListener(mOnStatusChangedListener);
                }
            }, 200);
        }
    }

    DownloadPresenter.OnStatusChangedListener mOnStatusChangedListener = new DownloadPresenter.OnStatusChangedListener() {
        @Override
        public void onStatusChanged(boolean isPlaying) {
            mPresenter.setTimingEnable(isPlaying);
        }
    };

    public void setBottomViewVisible(boolean visible) {
        if (mPlanInfo == null) {
            mBottomView.setVisibility(visible ? View.VISIBLE : View.GONE);
        } else {
            mBottomTimingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setCommentNumber(int number) {
        mBottomView.setCommentData(number);
    }

    @Override
    public void setRatingNumber(int number) {
        mBottomView.setRatingData(number);
    }

    @Override
    public void setRated(boolean rated) {
        mBottomView.setIsRated(rated);
    }

    @Override
    public void showTimingView() {
        mBottomView.setVisibility(View.GONE);
        mBottomTimingView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ABOVE, R.id.bottom_timing_view);
        mContentContainer.setLayoutParams(lp);
    }

    @Override
    public void setMaxPeriod(int minute) {
        System.out.println("minute: " + minute);
        mBottomTimingView.setTotalTime(minute);
    }

    @Override
    public void setCurrentPeriod(int currentSecond) {
        System.out.println("currentSecond: " + currentSecond);
        mBottomTimingView.setCurrentTime(currentSecond);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
