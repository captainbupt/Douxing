package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.domain.category.EnrollUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.PlanIntroductionView;

/**
 * Created by badou1 on 2015/7/30.
 */
public class PlanIntroductionPresenter  extends Presenter {

    PlanIntroductionView mPlanIntroductionView;
    CategoryDetail mCategoryDetail;
    EnrollUseCase mEnrollUseCase;
    String mRid;

    public PlanIntroductionPresenter(Context context, String rid) {
        super(context);
        this.mRid = rid;
    }

    @Override
    public void attachView(BaseView v) {
        mPlanIntroductionView = (PlanIntroductionView) v;
    }

    public void setData(CategoryDetail categoryDetail) {
        mPlanIntroductionView.setData(categoryDetail);
        this.mCategoryDetail = categoryDetail;
    }


    /*public void onSignClicked() {
        if (mCategoryDetail.getPlan().isStarted() && !mCategoryDetail.getEntry().isOffline() && mCategoryDetail.getEntry().getIn() == 0) {
            mPlanIntroductionView.showProgressDialog();
            if (mEnrollUseCase == null)
                mEnrollUseCase = new EnrollUseCase(mRid);
            mEnrollUseCase.setIsEnroll(true);
            mEnrollUseCase.execute(new BaseSubscriber(mContext) {
                @Override
                public void onResponseSuccess(Object data) {
                    mPlanIntroductionView.hideProgressDialog();
                    mPlanIntroductionView.showToast(R.string.entry_tip_enroll_success);
                    mPlanIntroductionView.setStatusText(R.string.entry_action_enroll_cancel, true, R.string.entry_status_check_ing);
                    mCategoryDetail.getEntry().setIn(1);
                }
            });
        } else if (mCategoryDetail.getEntry().isStarted() && !mCategoryDetail.getEntry().isOffline() && mCategoryDetail.getEntry().getIn() == 1) {
            mPlanIntroductionView.showProgressDialog();
            if (mEnrollUseCase == null)
                mEnrollUseCase = new EnrollUseCase(mRid);
            mEnrollUseCase.setIsEnroll(false);
            mEnrollUseCase.execute(new BaseSubscriber(mContext) {
                @Override
                public void onResponseSuccess(Object data) {
                    mPlanIntroductionView.hideProgressDialog();
                    mPlanIntroductionView.showToast(R.string.entry_tip_enroll_cancel_success);
                    mPlanIntroductionView.setStatusText(R.string.entry_action_enroll, true, -1);
                    mCategoryDetail.getEntry().setIn(0);
                }
            });
        }
    }
*/
}
