package com.badou.mworking.presenter.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.CommentActivity;
import com.badou.mworking.R;
import com.badou.mworking.TrainBaseActivity;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.category.CategoryRateUseCase;
import com.badou.mworking.domain.category.PeriodUpdateUseCase;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.PlanInfo;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.CategoryBaseView;
import com.badou.mworking.widget.RatingDialog;

import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class CategoryBasePresenter extends Presenter {

    public static final String KEY_RID = "rid";
    public static final int REQUEST_COMMENT = 126;

    CategoryBaseView mCategoryBaseView;
    String mRid;
    int mCategoryType;
    CategoryDetail mCategoryDetail;
    StoreUseCase mStoreUseCase;
    RatingDialog mRatingDialog;
    boolean isPaused;
    PlanInfo mPlanInfo;
    static Handler mPeriodHandler = new Handler();

    public CategoryBasePresenter(Context context, int type, String rid, PlanInfo planInfo) {
        super(context);
        this.mCategoryType = type;
        this.mRid = rid;
        this.mPlanInfo = planInfo;
    }

    @Override
    public void attachView(BaseView v) {
        mCategoryBaseView = (CategoryBaseView) v;
        getCategoryDetail(mRid);
    }

    protected void getCategoryDetail(final String rid) {
        mCategoryBaseView.showProgressDialog();
        new CategoryDetailUseCase(rid).execute(new BaseSubscriber<CategoryDetail>(mContext) {
            @Override
            public void onResponseSuccess(CategoryDetail data) {
                if (!isPaused)
                    mCategoryBaseView.setData(rid, data, mPlanInfo);
                setData(data);
            }

            @Override
            public void onCompleted() {
                mCategoryBaseView.hideProgressDialog();
            }

            @Override
            public void onErrorCode(int code) {
                mCategoryBaseView.showToast(R.string.tip_message_center_resource_gone);
                ((Activity) mContext).finish();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof IllegalStateException) { // 因为延迟操作，存在fragment在saveInstanceState之后commit的情况，catch这个错误，并在重新加载时显示
                    isPaused = true;
                } else {
                    super.onError(e);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COMMENT && resultCode == Activity.RESULT_OK && data != null) {
            int commentNumber = data.getIntExtra(CommentActivity.RESPONSE_COUNT, 0);
            mCategoryBaseView.setCommentNumber(commentNumber);
            // 存在Detail还没加载，就跳到评论的情况，做个保护
            if (mCategoryDetail != null)
                mCategoryDetail.setCcnt(commentNumber);
        }
    }

    public void setData(CategoryDetail categoryDetail) {
        this.mCategoryDetail = categoryDetail;
        mCategoryBaseView.setRated(categoryDetail.getRating() > 0);
        if (mPlanInfo != null) {
            mCategoryBaseView.setMaxPeriod(mPlanInfo.maxTimeMinute);
            mCategoryBaseView.setCurrentPeriod(mPlanInfo.currentTimeSecond);
            // 音频和视频不要自动开始
            if (mCategoryDetail.getFmt() != Constant.MWKG_FORAMT_TYPE_MPEG && mCategoryDetail.getFmt() != Constant.MWKG_FORAMT_TYPE_MP3)
                setTimingEnable(true);
        }
    }

    public void setTimingEnable(boolean isEnable) {
        if (mPlanInfo == null) {
            return;
        }
        if (mPlanInfo.currentTimeSecond / 60 > mPlanInfo.maxTimeMinute) {
            mPeriodHandler.removeCallbacks(mSecondRunnable);
            mCategoryBaseView.setCurrentPeriod(mPlanInfo.maxTimeMinute * 60);
            return;
        }
        if (isEnable) {
            if (mSecondRunnable == null) {
                mSecondRunnable = new SecondRunnable(new PeriodUpdateUseCase(mRid), mContext, mPlanInfo, mCategoryBaseView);
            }
            mCategoryBaseView.setCurrentPeriod(mPlanInfo.currentTimeSecond);
            mPeriodHandler.postDelayed(mSecondRunnable, 1000);
        } else {
            mPeriodHandler.removeCallbacks(mSecondRunnable);
        }
    }

    static SecondRunnable mSecondRunnable;

    static class SecondRunnable implements Runnable {

        PeriodUpdateUseCase mPeriodUpdateUseCase;
        private Context mContext;
        private PlanInfo mPlanInfo;
        private CategoryBaseView mCategoryBaseView;

        public SecondRunnable(PeriodUpdateUseCase periodUpdateUseCase, Context context, PlanInfo planInfo, CategoryBaseView categoryBaseView) {
            mPeriodUpdateUseCase = periodUpdateUseCase;
            mContext = context;
            mPlanInfo = planInfo;
            mCategoryBaseView = categoryBaseView;
        }

        @Override
        public void run() {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
            mPlanInfo.currentTimeSecond++;
            mCategoryBaseView.setCurrentPeriod(mPlanInfo.currentTimeSecond);
            if (mPlanInfo.currentTimeSecond % 60 == 0) {
                mPeriodUpdateUseCase.setTime(60);
                mPeriodUpdateUseCase.execute(new BaseSubscriber(mContext) {
                    @Override
                    public void onResponseSuccess(Object data) {

                    }
                });
            }
            if (mPlanInfo.currentTimeSecond / 60 < mPlanInfo.maxTimeMinute) {
                mPeriodHandler.postDelayed(this, 1000);
            }
        }
    }

    /**
     * 统计按钮跳转
     */
    public void onStatisticalClicked() {
        String titleStr = mContext.getResources().getString(R.string.statistical_data);
        String uid = UserInfo.getUserInfo().getUid();
        String url = Net.getRunHost() + Net.getTongji(uid, mRid);
        Intent intent = BackWebActivity.getIntent(mContext, titleStr, url);
        mContext.startActivity(intent);
    }

    /**
     * 点击收藏实现
     */
    public void onStoreClicked() {
        if (mStoreUseCase == null)
            mStoreUseCase = new StoreUseCase(mRid, Store.getStoreStringFromCategory(mCategoryType));
        mStoreUseCase.onStoreClicked(mContext, mCategoryBaseView, mCategoryDetail);
    }

    public void onCommentClicked() {
        ((Activity) mContext).startActivityForResult(CommentActivity.getIntent(mContext, mRid), REQUEST_COMMENT);
    }

    public void onRatingClicked() {
        if (mCategoryDetail == null) {
            mCategoryBaseView.showToast(R.string.message_wait);
            return;
        }
        if (mRatingDialog == null) {
            mRatingDialog = new RatingDialog(mContext, new RatingDialog.OnRatingConfirmListener() {

                @Override
                public void onRatingConfirm(int rating) {
                    uploadRating(rating);
                    mCategoryDetail.setRating(rating);
                    mCategoryDetail.setEcnt(mCategoryDetail.getEcnt() + 1);
                    mCategoryBaseView.setRatingNumber(mCategoryDetail.getEcnt());
                }
            });
        }
        mRatingDialog.setCurrentScore(mCategoryDetail.getRating());
        mRatingDialog.show();
    }

    /**
     * 功能描述: 提交课件评分
     */
    private void uploadRating(int rating) {
        mCategoryBaseView.hideProgressDialog();
        CategoryRateUseCase categoryRateUseCase = new CategoryRateUseCase(mRid);
        categoryRateUseCase.setCredit(rating);
        categoryRateUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (mRatingDialog != null) {
                    mRatingDialog.dismiss();
                }
                mCategoryBaseView.setRated(true);
            }

            @Override
            public void onCompleted() {
                mCategoryBaseView.hideProgressDialog();
            }
        });
    }

    public void onShareClicked() {
        if (mCategoryDetail == null) {
            mCategoryBaseView.showToast(R.string.message_wait);
            return;
        }
        if (TextUtils.isEmpty(mCategoryDetail.getShareUrl())) {
            mCategoryBaseView.showToast(R.string.share_forbid);
            return;
        }
        OnekeyShare oks = new OnekeyShare();

        oks.setTitle(mCategoryDetail.getSubject());
        oks.setText(mCategoryDetail.getSubject());
        oks.setImageUrl("http://ops.mworking.cn/webview/public/img/app_wxshare_icon.png");
        oks.setUrl(mCategoryDetail.getShareUrl());
        oks.setDialogMode();
        // 启动分享GUI
        oks.show(mContext);
    }

    public CategoryDetail getData() {
        return mCategoryDetail;
    }

    @Override
    public void destroy() {
        setTimingEnable(false);
        mPeriodHandler.removeCallbacks(mSecondRunnable);
        mSecondRunnable = null;
    }

    @Override
    public void pause() {
        mPeriodHandler.removeCallbacks(mSecondRunnable);
    }

    @Override
    public void resume() {
        if (isPaused && mCategoryBaseView != null && mCategoryDetail != null) {
            mCategoryBaseView.setData(mRid, mCategoryDetail, mPlanInfo);
        }
        if (mSecondRunnable != null)
            mPeriodHandler.postDelayed(mSecondRunnable, 1000);
        isPaused = false;
    }

    private final String KEY_TIME = "timer";

    public void onSaveInstanceState(Bundle outState) {
        if (mPlanInfo != null)
            outState.putInt(KEY_TIME, mPlanInfo.currentTimeSecond);
    }

    public void onRestoreInstanceState(Bundle outState) {
        if (outState != null && mPlanInfo != null) {
            mPlanInfo.currentTimeSecond = outState.getInt(KEY_TIME, 0);
        }
    }
}
