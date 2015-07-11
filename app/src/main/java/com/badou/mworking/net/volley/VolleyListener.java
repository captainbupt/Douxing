package com.badou.mworking.net.volley;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by yee on 3/7/14.
 */
public abstract class VolleyListener<T> implements Response.ErrorListener,
        Response.Listener {

    private Context mContext;

    public VolleyListener(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public void onResponse(Object responseObject) {
        onCompleted();
        JSONObject response = (JSONObject) responseObject;
        int code = response.optInt(Net.CODE);
        if (code == Net.LOGOUT) {
            AppApplication.logoutShow(mContext);
            return;
        }
        if (code != Net.SUCCESS) {
            onErrorCode(code);
            return;
        }
        onResponseSuccess(response);
    }

    public void onCompleted() {
    }

    public abstract void onResponseSuccess(JSONObject response);

    public void onErrorCode(int code) {
        try {
            Field field = R.string.class.getField("error_code_" + code);
            int i = field.getInt(new R.string());
            ToastUtil.showToast(mContext, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        onCompleted();
        ToastUtil.showToast(mContext, R.string.error_service);
        onErrorCode(-1);
/*        if (error instanceof ResponseError) {
            ToastUtil.showToast(mContext, error.getMessage());
        }*/
    }
}
