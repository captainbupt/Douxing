package com.badou.mworking.presenter.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.CommentActivity;
import com.badou.mworking.R;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.category.CategoryRateUseCase;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.CategoryBaseView;
import com.badou.mworking.widget.RatingDialog;

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

    public CategoryBasePresenter(Context context, int type, String rid) {
        super(context);
        this.mCategoryType = type;
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        mCategoryBaseView = (CategoryBaseView) v;
        mCategoryBaseView.showProgressDialog();
        new CategoryDetailUseCase(mRid).execute(new BaseSubscriber<CategoryDetail>(mContext) {
            @Override
            public void onResponseSuccess(CategoryDetail data) {
                if (!isPaused)
                    mCategoryBaseView.setData(mRid, data);
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

    public void onSettingClicked() {
        mCategoryBaseView.showToast("跳转计划转详情面");
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
        categoryRateUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (mRatingDialog != null) {
                    mRatingDialog.dismiss();
                }
            }

            @Override
            public void onCompleted() {
                mCategoryBaseView.hideProgressDialog();
            }
        });
    }

    public CategoryDetail getData() {
        return mCategoryDetail;
    }

    @Override
    public void resume() {
        if (isPaused && mCategoryBaseView != null && mCategoryDetail != null) {
            mCategoryBaseView.setData(mRid, mCategoryDetail);
        }
        isPaused = false;
    }
}
