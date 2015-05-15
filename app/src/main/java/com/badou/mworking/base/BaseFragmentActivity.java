package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

import com.badou.mworking.R;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.widget.SwipeBackLayout;

public abstract class BaseFragmentActivity extends FragmentActivity {
	
	protected Context mContext;
	protected Activity mActivity;
	
	public SwipeBackLayout layout;      
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
		mActivity = this;
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		AppManager.getAppManager().addActivity(this);
	}
	
	@Override
	public void finish() {
		//将当前Activity移除掉
		AppManager.getAppManager().removeActivity(this);
		super.finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}
