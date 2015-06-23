package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.widget.OptimizedImageView;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 * 启动页面
 */
public class SplashActivity extends BaseNoTitleActivity {

	public static final String KEY_IS_FIRST = AppApplication.appVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		OptimizedImageView backgroundImage = (OptimizedImageView) findViewById(R.id.iv_activity_splash);
		backgroundImage.setImageResourceFullScreen(R.drawable.background_splash);
		
		String lang = SP.getStringSP(this, SP.DEFAULTCACHE, Constant.LANGUAGE, "zh");
		//en为英文版，取值zh为中文版。
		changeLanguage(lang);

		disableSwipeBack();
		// 等待1-2秒后进入后续界面
		new Handler().postDelayed(new JumpRunnable(mContext), 1500);

		// 设置友盟统计模式可以同时统计Activity和Fragment
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(mContext);
	}

	class JumpRunnable implements Runnable{

		private Context mContext;

		public JumpRunnable(Context context){
			this.mContext = context;
		}

		@Override
		public void run() {
			//判断是否是第一次启动程序
			if (!SP.getBooleanSP(mContext, SP.DEFAULTCACHE, KEY_IS_FIRST, true)) {
				//查看shareprefernces中是否保存的UserInfo(登录时保存的)
				UserInfo userInfo = UserInfo.getUserInfo(getApplicationContext());
				if (userInfo == null) {
					goLogin();
				} else {
					goMain(userInfo);
				}
			} else {
				SP.clearSP(mContext, SP.DEFAULTCACHE);
				//软件运行过sp中记录
				SP.putBooleanSP(mContext, SP.DEFAULTCACHE, KEY_IS_FIRST, false);
				goIntroduction();
			}
		}
	}


	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	/**
	 * 功能描述:跳转到登录页面
	 */
	private void goLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 功能描述: 跳转到主页面
	 */
	private void goMain(UserInfo userInfo) {
		Intent intent = new Intent(this, MainGridActivity.class);
		//把用户信息保存到Application
		((AppApplication) getApplication()).setUserInfo(userInfo);
		startActivity(intent);
		finish();
	}

	/**
	 * 功能描述: 跳转到引导页面
	 */
	private void goIntroduction() {
		Intent intent = new Intent(this, IntroductionActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 功能描述: 更换语言
	 */
	private void changeLanguage(String lang){/*
		Resources resources = getResources();// 获得res资源对象
		((AppApplication) getApplication()).changeAppLanguage(resources,lang);*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		//极光推送
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 极光推送
		JPushInterface.onResume(this);
	}
}

