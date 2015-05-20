package com.badou.mworking;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 类:  <code> ChangeLanguageActivity </code>
 * 功能描述: 语言更换Activity
 * 创建人:  葛建锋
 * 创建日期: 2014年10月20日 上午10:06:42
 * 开发环境: JDK7.0
 */
public class ChangeLanguageActivity extends BaseNoTitleActivity implements OnClickListener{
	
	private TextView actionbarTitleTv;
	private RelativeLayout chineseRelay;  //中文
	private RelativeLayout englishReylay;  //英文
	private RadioButton chineseRad;      //中文单选框
	private RadioButton englishRad;    //英文单选框
	private ImageView ivBack;        //返回键
	
	private SwipeBackLayout layout;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.changelanguageactivity);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		initView();
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	protected void initView(){
		actionbarTitleTv = (TextView) findViewById(R.id.txt_actionbar_title);
		ivBack = (ImageView) findViewById(R.id.iv_actionbar_left);
		actionbarTitleTv.setText(getResources().getString(R.string.more_laguage));
		chineseRelay = (RelativeLayout) findViewById(R.id.chinese_relay);
		englishReylay = (RelativeLayout) findViewById(R.id.english_relay);
		chineseRad = (RadioButton) findViewById(R.id.chinese_radbtn);
		englishRad = (RadioButton) findViewById(R.id.english_radbtn);
		chineseRelay.setOnClickListener(this);
		englishReylay.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		String language = SP.getStringSP(this, SP.DEFAULTCACHE,Constant.LANGUAGE, "zh");
		if(language!=null&&!language.equals("")){
			if(language.equals("en")){
				chineseRad.setChecked(false);
				englishRad.setChecked(true);
			}else{
				chineseRad.setChecked(true);
				englishRad.setChecked(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chinese_relay:
			chineseRad.setChecked(true);
			englishRad.setChecked(false);
			SP.putStringSP(ChangeLanguageActivity.this,SP.DEFAULTCACHE,Constant.LANGUAGE, "zh");
			setLanguage("zh");
			break;
		case R.id.english_relay:
			chineseRad.setChecked(false);
			englishRad.setChecked(true);
			SP.putStringSP(ChangeLanguageActivity.this,SP.DEFAULTCACHE,Constant.LANGUAGE, "en");
			setLanguage("en");
			break;
			
		case R.id.iv_actionbar_left:
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 功能描述:语言设置
	 */
	public void setLanguage(String language){
		 Resources resources = getResources();// 获得res资源对象
		((AppApplication) getApplication()).changeAppLanguage(resources,language);
		Intent intent = new Intent();
		intent.setClass(this, MainGridActivity.class);
		AppManager.finishAllActivity();
		this.startActivity(intent);
		finish();
	}
}
