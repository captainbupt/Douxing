package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.domain.ExperienceInfoUseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ExperienceInformationView;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperienceInformationPresenter extends Presenter {

    ExperienceInformationView mExperienceInformationView;

    public ExperienceInformationPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mExperienceInformationView = (ExperienceInformationView) v;
        mExperienceInformationView.disableConfirmButton();
    }

    Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    public void onTextChanged(String name, String phone, String company, String job) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || phone.length() != 11 || TextUtils.isEmpty(company) || TextUtils.isEmpty(job)) {
            mExperienceInformationView.disableConfirmButton();
        } else {
            Matcher m = p.matcher(phone);
            if (m.matches()) {
                mExperienceInformationView.enableConfirmButton();
            } else {
                mExperienceInformationView.disableConfirmButton();
            }
        }
    }

    public void onConfirmed(String name, String phone, String company, String job) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || phone.length() != 11 || TextUtils.isEmpty(company) || TextUtils.isEmpty(job)) {
            return;
        }
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

            @Override
            public void onErrorCode(int code) {
                try {
                    Field field = R.string.class.getField("error_code_" + code);
                    int i = field.getInt(new R.string());
                    ToastUtil.showToast(mContext, i);
                } catch (Exception e) {
                    e.printStackTrace();
                    mExperienceInformationView.showToast("录入完整信息，有助于兜行为你匹配一个充实、具体的体验环境");
                }
            }
        });
    }

    public void onCancel() {
        ((Activity) mContext).finish();
    }

    public void onBack() {
        mContext.startActivity(LoginActivity.getIntent(mContext));
    }
}
