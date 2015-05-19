package com.badou.mworking.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;

import com.badou.mworking.R;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;

public class BaseNoTitleActivity extends Activity{
	
	protected Context mContext;
	protected Activity mActivity;
	protected Intent mReceivedIntent;
	
	protected SwipeBackLayout layout;

	protected WaitProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		mActivity = this;
		mReceivedIntent = getIntent();
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		AppManager.getAppManager().addActivity(this);
		mProgressDialog = new WaitProgressDialog(mContext);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProgressDialog.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void finish() {
		//将当前Activity移除掉
		AppManager.getAppManager().removeActivity(this);
		super.finish();
	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
}

