package com.badou.mworking.net;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.util.ToastUtil;

import java.lang.reflect.Field;
import java.util.List;

import rx.Subscriber;

public abstract class BaseListSubscriber<T> extends Subscriber<BaseNetListEntity<T>> {

    private Context mContext;

    public BaseListSubscriber(Context mContext) {
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
    public void onNext(BaseNetListEntity<T> baseNetEntity) {
        if (baseNetEntity.getErrcode() == Net.LOGOUT) {
            AppApplication.logoutShow(mContext);
            return;
        }
        if (baseNetEntity.getErrcode() != Net.SUCCESS) {
            onErrorCode(baseNetEntity.getErrcode());
            return;
        }
        onResponseSuccess(baseNetEntity.getData());
    }

    public abstract void onResponseSuccess(List<T> data);

    public void onErrorCode(int code) {
        try {
            Field field = R.string.class.getField("error_code_" + code);
            int i = field.getInt(new R.string());
            ToastUtil.showToast(mContext, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
