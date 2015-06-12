package com.badou.mworking.net.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

/**
 * Created by yee on 3/7/14.
 */
public abstract class VolleyListener implements Response.ErrorListener,
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
        ToastUtil.showToast(mContext, "错误码: " + code);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        onCompleted();
        ToastUtil.showNetExc(mContext);
        onErrorCode(-1);
/*        if (error instanceof ResponseError) {
            ToastUtil.showToast(mContext, error.getMessage());
        }*/
    }
}
