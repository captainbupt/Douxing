package com.badou.mworking.net;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.util.ToastUtil;

import java.lang.reflect.Field;

import rx.Subscriber;

public abstract class BaseSubscriber<T> extends Subscriber<BaseNetEntity<T>> {

    private Context mContext;

    public BaseSubscriber(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        onCompleted();
        e.printStackTrace();
        ToastUtil.showToast(mContext, R.string.error_service);
    }

    @Override
    public void onNext(BaseNetEntity<T> baseNetEntity) {
        if (baseNetEntity.getErrcode() == Net.LOGOUT) {
            AppApplication.logoutShow(mContext);
            return;
        }
        if (baseNetEntity.getErrcode() != Net.SUCCESS) {
            onErrorCode(baseNetEntity.getErrcode());
            return;
        }
        onResponseSuccess(baseNetEntity.data);
    }

    public abstract void onResponseSuccess(T data);

    public void onErrorCode(int code) {
        try {
            Field field = R.string.class.getField("error_code_" + code);
            int i = field.getInt(new R.string());
            ToastUtil.showToast(mContext, i);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(mContext, R.string.error_service);
        }
    }

}
