package com.badou.mworking.presenter.category;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.domain.category.EnrollUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.EntryIntroductionView;

public class EntryIntroductionPresenter extends Presenter {

    EntryIntroductionView mEntryIntroductionView;
    CategoryDetail mCategoryDetail;
    EnrollUseCase mEnrollUseCase;
    String mRid;

    public EntryIntroductionPresenter(Context context, String rid) {
        super(context);
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        mEntryIntroductionView = (EntryIntroductionView) v;
    }

    public void setData(CategoryDetail categoryDetail) {
        mEntryIntroductionView.setData(categoryDetail);
        this.mCategoryDetail = categoryDetail;
    }

    public void onSignClicked() {
        if (mCategoryDetail.getEntry().isStarted() && !mCategoryDetail.getEntry().isOffline() && mCategoryDetail.getEntry().getIn() == 0) {
            mEntryIntroductionView.showProgressDialog();
            if (mEnrollUseCase == null)
                mEnrollUseCase = new EnrollUseCase(mRid);
            mEnrollUseCase.setIsEnroll(true);
            mEnrollUseCase.execute(new BaseSubscriber(mContext) {
                @Override
                public void onResponseSuccess(Object data) {
                    mEntryIntroductionView.hideProgressDialog();
                    mEntryIntroductionView.showToast(R.string.entry_tip_enroll_success);
                    mEntryIntroductionView.setStatusText(R.string.entry_action_enroll_cancel, true, R.string.entry_status_check_ing);
                    mCategoryDetail.getEntry().setIn(1);
                    mCategoryDetail.getEntry().incrementEnroll();
                    mEntryIntroductionView.setData(mCategoryDetail);
                }
            });
        } else if (mCategoryDetail.getEntry().isStarted() && !mCategoryDetail.getEntry().isOffline() && mCategoryDetail.getEntry().getIn() == 1) {
            mEntryIntroductionView.showProgressDialog();
            if (mEnrollUseCase == null)
                mEnrollUseCase = new EnrollUseCase(mRid);
            mEnrollUseCase.setIsEnroll(false);
            mEnrollUseCase.execute(new BaseSubscriber(mContext) {
                @Override
                public void onResponseSuccess(Object data) {
                    mEntryIntroductionView.hideProgressDialog();
                    mEntryIntroductionView.showToast(R.string.entry_tip_enroll_cancel_success);
                    mEntryIntroductionView.setStatusText(R.string.entry_action_enroll, true, -1);
                    mCategoryDetail.getEntry().setIn(0);
                    mCategoryDetail.getEntry().decrementEnroll();
                    mEntryIntroductionView.setData(mCategoryDetail);
                }
            });
        }
    }

}
