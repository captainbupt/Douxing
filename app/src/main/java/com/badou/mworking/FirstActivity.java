/* 
 * 文件名: FirstActivity.java
 * 包路径: com.badou.mworking
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年10月21日 上午11:43:38
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年10月21日 上午11:43:38 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 * 类:  <code> FirstActivity </code>
 * 功能描述:  更换语言页面，程序每次启动都会先进入该界面，获取程序中设置的语言，应为该activity只有功能没有界面，所以
 *           主题是透明的，但是必须要给定相应的布局，并且在布局中设置一个背景色，否则透明主题会有问题
 * 创建人:  葛建锋
 * 创建日期: 2014年10月21日 上午11:43:38
 * 开发环境: JDK7.0
 */
public class FirstActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstactivity);
		String lang = SP.getStringSP(this,SP.DEFAULTCACHE, Constant.LANGUAGE, "zh");
		//en为英文版，取值zh为中文版。
		changeLanguage(lang); 
		Intent intent = new Intent();
		intent.setClass(this, SplashActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	/**
	 * 功能描述: 更换语言
	 */
	private void changeLanguage(String lang){
		Resources resources = getResources();// 获得res资源对象
		((AppApplication) getApplication()).changeAppLanguage(resources,lang);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//极光推送
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// 极光推送
		JPushInterface.onResume(this);
	}
}
