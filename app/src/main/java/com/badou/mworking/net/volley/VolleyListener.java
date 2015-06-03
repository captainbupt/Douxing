package com.badou.mworking.net.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

/**
 * Created by yee on 3/7/14.
 */
public abstract class VolleyListener implements Response.ErrorListener,
        Response.Listener {
    public VolleyListener(Context mContext) {
        super();
        this.mContext = mContext;
    }

    private Context mContext;

    public void onStart() {
    }

    @Override
    public void onResponse(Object responseObject) {
        JSONObject response = (JSONObject) responseObject;
        int code = response.optInt(Net.CODE);
        if (code != Net.SUCCESS) {
            ToastUtil.showNetExc(mContext);
            return;
        }
        onResponseData(response
                .optJSONObject(Net.DATA));
    }

    public void onResponseData(JSONObject jsonObject) {
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof ResponseError) {
            ToastUtil.showToast(mContext, error.getMessage());
        } else
            ToastUtil.showNetExc(mContext);
    }
}
