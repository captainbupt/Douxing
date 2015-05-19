package com.badou.mworking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 类: <code> AboutUsActivity </code> 功能描述: 关于我们 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午4:04:11 开发环境: JDK7.0
 */
public class AboutUsActivity extends BaseBackActionBarActivity implements OnClickListener{

	private TextView infoTextView;
	private ProgressDialog mProgressDialog;
	private CheckBox chkPicShow;  //是否显示图片
	private CheckBox pushChk;    //是否推送提醒
	
	private LinearLayout updateLinear;    // 检查更新
	private LinearLayout clearCacheLinear;  //缓存管理
	private LinearLayout frequentlyQesLinear; //常见问题
	private LinearLayout contactUsLinear;  //联系我们
	private LinearLayout changeLaguageLinear; //语言设置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionbarTitle(mContext.getResources().getString(
				R.string.title_name_about));
		setContentView(R.layout.activity_about_us);
		layout.attachToActivity(this);
		initView();
		Boolean isImgChk = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,"pic_show", false); 
		chkPicShow.setChecked(isImgChk);
		Boolean isOpenPush = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,Constant.PUSH_NOTIFICATIONS, true);
		pushChk.setChecked(isOpenPush);
		infoTextView.setText(mContext.getResources().getString(
				R.string.app_name)
				+ AppApplication.appVersion);
		initOption();
	}

	protected void initView(){
		super.initView();
		infoTextView = (TextView) findViewById(R.id.tv_user_setting_info);
		chkPicShow = (CheckBox) findViewById(R.id.chk_pic);
		pushChk = (CheckBox) findViewById(R.id.push_chk);
		updateLinear = (LinearLayout) findViewById(R.id.update_linear);
		clearCacheLinear = (LinearLayout) findViewById(R.id.clear_cache_linear);
		frequentlyQesLinear = (LinearLayout) findViewById(R.id.frequently_qes_linear);
		contactUsLinear = (LinearLayout) findViewById(R.id.contact_us_linear);
		changeLaguageLinear = (LinearLayout) findViewById(R.id.change_laguage_linear);
		updateLinear.setOnClickListener(this);
		clearCacheLinear.setOnClickListener(this);
		frequentlyQesLinear.setOnClickListener(this);
		contactUsLinear.setOnClickListener(this);
		changeLaguageLinear.setOnClickListener(this);
	}
	
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	private void initOption() {
		
		//是否显示图片开关
		chkPicShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {      
					SP.putBooleanSP(mContext, SP.DEFAULTCACHE,"pic_show", true);
				} else {		 
					SP.putBooleanSP(mContext, SP.DEFAULTCACHE,"pic_show", false);
				}
			}
		});
		
		//是否开启推送开关
		pushChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){		
					SP.putBooleanSP(mContext,SP.DEFAULTCACHE, Constant.PUSH_NOTIFICATIONS, true);
					JPushInterface.resumePush(getApplicationContext());	//推送打开
					AlarmUtil alarmUtil = new AlarmUtil();
					//alarmUtil.OpenTimer(AboutUsActivity.this);
				}else{		
					SP.putBooleanSP(mContext,SP.DEFAULTCACHE, Constant.PUSH_NOTIFICATIONS, false);
					JPushInterface.stopPush(getApplicationContext());   //推送关闭
					AlarmUtil alarmUtil = new AlarmUtil();
					//alarmUtil.cancel(AboutUsActivity.this);
				}
			}
		});
	}

	private void checkUpdate(boolean isAuto) {
		if (!isAuto) {
			if (mProgressDialog == null)
				mProgressDialog = new WaitProgressDialog(mContext,
						R.string.action_update_check_ing);
			mProgressDialog.setTitle(R.string.message_tips);
			if (null != mProgressDialog && mContext != null
					&& !mActivity.isFinishing()) {
				mProgressDialog.show();
			}
		}
		ServiceProvider.doCheckUpdate(mContext, null, new VolleyListener(
				mContext) {

			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				try {
					int code = response.optInt(Net.CODE);
					if (code != Net.SUCCESS) {
						ToastUtil.showToast(mContext, "code:" + code);
						ToastUtil.showToast(mContext, R.string.result_update_check_fail);
						return;
					}
					JSONObject data = response.optJSONObject(Net.DATA);
					JSONObject newver = data
							.optJSONObject(RequestParams.CHK_UPDATA_PIC_NEWVER);
					boolean hasNew = newver
							.optInt(ResponseParams.CHECKUPDATE_NEW) == 1;
					if (hasNew) {
						final String info = newver
								.optString(ResponseParams.CHECKUPDATE_INFO);
						final String url = newver
								.optString(ResponseParams.CHECKUPDATE_URL);
						new AlertDialog.Builder(mContext)
						.setTitle(R.string.main_tips_update_title).setMessage(info)
								.setPositiveButton(R.string.about_btn_update,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												ServiceProvider
														.doUpdateMTraning(
																mActivity, url);
											}
										}).setNegativeButton(R.string.text_cancel, null)
								.create().show();
					} else {
						ToastUtil.showToast(mContext, R.string.result_update_check_noneed);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (null != mProgressDialog && mContext != null
						&& !mActivity.isFinishing()) {
					mProgressDialog.dismiss();
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				super.onErrorResponse(error);
				if (null != mProgressDialog && mContext != null
						&& !mActivity.isFinishing()) {
					mProgressDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_linear:
			checkUpdate(false);
			break;
		case R.id.clear_cache_linear:
			Intent intent = new Intent(mContext,ClearCacheActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.frequently_qes_linear:
			Intent intent1 = new Intent(mContext,
					BackWebActivity.class);
			intent1.putExtra(BackWebActivity.VALUE_URL,
					Net.getRunHost(AboutUsActivity.this) + Net.FAQ);
			intent1.putExtra(BackWebActivity.VALUE_TITLE,
					mContext.getResources().getString(R.string.title_name_Wenti));
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.contact_us_linear:
			new AlertDialog.Builder(mContext)
			.setMessage(R.string.about_tips_phone
					)
			.setPositiveButton(
					R.string.about_btn_tophone,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							Intent intent = new Intent(
									"android.intent.action.CALL",
									Uri.parse("tel:4008233773"));
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						}
					}).setNegativeButton(R.string.text_cancel, null)
			.create().show();
			break;
		case R.id.change_laguage_linear:
			Intent intent2 = new Intent(mContext,ChangeLanguageActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		default:
			break;
		}
	}
}
