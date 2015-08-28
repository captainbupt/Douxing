package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.domain.ExperienceInfoUseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ExperienceInformationView;

public class ExperienceInformationPresenter extends Presenter {

    ExperienceInformationView mExperienceInformationView;

    public ExperienceInformationPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mExperienceInformationView = (ExperienceInformationView) v;
    }

    public void onTextChanged(String name, String phone, String company, String job) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(company) || TextUtils.isEmpty(job)) {
            mExperienceInformationView.disableConfirmButton();
        } else {
            mExperienceInformationView.enableConfirmButton();
        }
    }

    public void onConfirmed(String name, String phone, String company, String job) {
        mExperienceInformationView.showProgressDialog();
        new ExperienceInfoUseCase(name, phone, company, job).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ((Activity) mContext).finish();
            }

            @Override
            public void onCompleted() {
                mExperienceInformationView.hideProgressDialog();
                super.onCompleted();
            }
        });
    }

    public void onCancel() {
        ((Activity) mContext).finish();
    }

    public void onBack() {
        ((Activity) mContext).finish();
    }
}
