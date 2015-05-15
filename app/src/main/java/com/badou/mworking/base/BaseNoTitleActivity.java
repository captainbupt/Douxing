package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;

public class BaseNoTitleActivity extends Activity{
	
	protected Context mContext;
	protected Activity mActivity;
	
	protected SwipeBackLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mActivity = this;
		AppManager.getAppManager().addActivity(this);
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void finish() {
		//将当前Activity移除掉
		AppManager.getAppManager().removeActivity(this);
		super.finish();
	}

	protected void showToast(String message) {
		ToastUtil.showToast(mContext, message);
	}

	protected void showToast(int message) {
		ToastUtil.showToast(mContext, message);
	}
}

