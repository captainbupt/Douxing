package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.badou.mworking.base.BaseFragmentActivity;

/**
 * 类:  <code> SplashActivity </code>
 * 功能描述: 启动页面
 * 创建人: 葛建锋
 * 创建日期: 2013-11-8 上午10:11:14
 * 开发环境: JDK6.0
 */
public class SplashActivity extends BaseFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashactivity);
		 new Handler().postDelayed(new Runnable() {  
	            public void run() {  
	                Intent mainIntent = new Intent(SplashActivity.this,  
	                        IntroductionActivity.class);  
	                SplashActivity.this.startActivity(mainIntent);  
	                SplashActivity.this.finish();  
	            }
	        }, 2000);  
	}
}

