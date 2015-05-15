package com.badou.mworking.net.volley;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.badou.mworking.util.ToastUtil;

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

	public abstract void onResponse(Object responseObject);

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		if (error instanceof ResponseError) {
			ToastUtil.showToast(mContext, error.getMessage());
		} else
			ToastUtil.showNetExc(mContext);
	}
}
