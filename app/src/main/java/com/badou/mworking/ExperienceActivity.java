/* 
 * 文件名: ExperienceActivity.java
 * 包路径: com.badou.mworking.widget
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年8月8日 下午5:52:52
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年8月8日 下午5:52:52 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.MD5;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * 类: <code> ExperienceActivity </code> 
 * 功能描述: 快速体验, 该类已经没有用了，点击快速体验直接使用匿名账户进入，但后期会开通在线注册，如果可以的话，在这个类修改
 * 创建人: 葛建锋 
 * 创建日期: 2014年8月8日 下午5:52:52
 * 开发环境: JDK7.0
 */
public class ExperienceActivity extends BaseBackActionBarActivity implements
		OnClickListener {

	private EditText etPhone;   //电话
	private EditText etVerify;  //验证码
	private EditText etCompany;  //公司
	private EditText etOffice;  //公司
	private TextView btnOK;
	private TextView btnGetMsg;
	private TextView tvWuFaHuoQu;
	private TextView tvTiShi;
	private static final int totalTime = 120;
	private int recLen = totalTime;
	private ProgressDialog mp;
	
	private SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tiyan);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		setActionbarTitle(getResources().getString(
				R.string.login_name_experience));
		initView();
		initListener();
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 
	 * 功能描述:初始化控件
	 * 
	 * @param view
	 */
	private void initView() {
		mp = new WaitProgressDialog(mContext, R.string.login_action_login_ing);
		etPhone = (EditText) findViewById(R.id.et_input_phone);
		etVerify = (EditText) findViewById(R.id.et_Verify);
		etCompany = (EditText) findViewById(R.id.et_company);
		etOffice = (EditText) findViewById(R.id.et_office);
		btnGetMsg = (TextView) findViewById(R.id.btn_get_duanXin);
		btnOK = (TextView) findViewById(R.id.btn_ok);
		tvWuFaHuoQu = (TextView) findViewById(R.id.tv_weiShouDao);
		tvTiShi = (TextView) this.findViewById(R.id.tv_tishi);

		btnOK.setText( mContext.getResources().getString(R.string.login_name_experience));
		tvTiShi.setText(mContext.getResources().getString(
				R.string.act_exprience_top_msg));
		btnGetMsg.setText(mContext.getResources().getString(
				R.string.TiYan_btn_getCode));
		sendBtnCannotClick();
	}

	/**
	 * 
	 * 功能描述: 设置控件的监听
	 */
	private void initListener() {
		btnGetMsg.setOnClickListener(this);
		btnOK.setOnClickListener(this);
		tvWuFaHuoQu.setOnClickListener(this);

		etPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				int len = etPhone.getText().toString().trim().length();
				if (len < 11) {
					sendBtnCannotClick();
				} else {
					sendBtnCanClick();
				}
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			recLen--;
			if (recLen >= 0) {
				if (recLen == 0) {
					sendCanClickInput();
					btnGetMsg.setText(mContext.getResources().getString(
							R.string.TiYan_btn_getCode));
					recLen = totalTime;
					return;
				} else {
					sendCannotClickInput();
					btnGetMsg.setText(recLen + mContext.getResources().getString(
							R.string.TiYan_btn_RegetCode));
					handler.postDelayed(this, 1000);
				}
			} else {
				recLen = totalTime;
			}
		}
	};

	/**
	 * 可以点击&可以输入
	 */
	private void sendCanClickInput() {
		int bottom = btnGetMsg.getPaddingBottom();
		int top = btnGetMsg.getPaddingTop();
		int right = btnGetMsg.getPaddingRight();
		int left = btnGetMsg.getPaddingLeft();
		btnGetMsg.setBackgroundResource(R.drawable.btn_bg_blue);
		btnGetMsg.setPadding(left, top, right, bottom);
		btnGetMsg.setEnabled(true);
		etPhone.setEnabled(true);
		btnGetMsg.setText(mContext.getResources().getString(
				R.string.TiYan_btn_getCode));

	}

	/**
	 * 发送短信验证btn不可点击 & 输入框不可输入
	 */
	private void sendCannotClickInput() {
		btnGetMsg.setEnabled(false);
		etPhone.setEnabled(false);
		int bottom = btnGetMsg.getPaddingBottom();
		int top = btnGetMsg.getPaddingTop();
		int right = btnGetMsg.getPaddingRight();
		int left = btnGetMsg.getPaddingLeft();
		btnGetMsg.setBackgroundResource(R.color.color_login_button_bg_not);
		btnGetMsg.setPadding(left, top, right, bottom);

	}

	/**
	 * 可以点击
	 */
	private void sendBtnCannotClick() {
		btnGetMsg.setEnabled(false);
		int bottom = btnGetMsg.getPaddingBottom();
		int top = btnGetMsg.getPaddingTop();
		int right = btnGetMsg.getPaddingRight();
		int left = btnGetMsg.getPaddingLeft();
		btnGetMsg.setBackgroundResource(R.color.color_login_button_bg_not);
		btnGetMsg.setPadding(left, top, right, bottom);

	}

	private void sendBtnCanClick() {
		btnGetMsg.setEnabled(true);
		int bottom = btnGetMsg.getPaddingBottom();
		int top = btnGetMsg.getPaddingTop();
		int right = btnGetMsg.getPaddingRight();
		int left = btnGetMsg.getPaddingLeft();
		btnGetMsg.setBackgroundResource(R.drawable.btn_bg_blue);
		btnGetMsg.setPadding(left, top, right, bottom);
	}

	@Override
	public void onClick(View arg0) {
		if (ToastUtil.showNetExc(this)) {
			return;
		}
		// 点击监听事件
		switch (arg0.getId()) {
		case R.id.btn_get_duanXin:// 获取验证码
			String str = etPhone.getText().toString().trim();
			if (etPhone.getText().toString().trim().equals("")) {
				ToastUtil.showToast(mContext, R.string.act_yanZheng_phone_notnull);
			} else if (etPhone.getText().toString().trim().length() < 11) {
				ToastUtil.showToast(mContext, R.string.tips_phone_woring);
			} else {
				sendCannotClickInput();
				handler.postDelayed(runnable, 1000);
				getVerificationCode(str);
			}
			break;
		case R.id.btn_ok:// 验证
			verification();
			break;
		case R.id.tv_weiShouDao:// 无法获取 跳转webview
			Intent intent = new Intent(mContext, TipsWebView.class);
			intent.putExtra(BackWebActivity.VALUE_URL,
					"http://mworking.cn/badou/verify-help.html");
			intent.putExtra(TipsWebView.KEY_TipsWebView,
					TipsWebView.ACT_TipsWebView);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	
	/**
	 * 验证
	 */
	private void verification(){
		//判断电话号码是否为空
		String phoneStr = etPhone.getText().toString().trim();
		if (TextUtils.isEmpty(phoneStr)) {
			ToastUtil.showToast(mContext, mContext.getResources()
					.getString(R.string.act_yanZheng_phone_notnull));
			return;
		} 
		//判断验证码输入是否为空
		String verifyStr = etVerify.getText().toString().trim();
		if (TextUtils.isEmpty(verifyStr)) {
			ToastUtil.showToast(mContext, mContext.getResources()
					.getString(R.string.act_yanZheng_verfiy_notnull));
			return;
		} 
		//判断公司输入是否为空
		String companyStr = etCompany.getText().toString().trim();
		if(TextUtils.isEmpty(companyStr)){
			ToastUtil.showToast(mContext, mContext.getResources()
					.getString(R.string.act_expri_company_null_vcode));
			return;
		}else{
			// 公司名称大于2个字符
			if(companyStr.length()<2){
				ToastUtil.showToast(mContext, mContext.getResources()
						.getString(R.string.act_expri_company_err_vcode));
				return;
			}
		}
		// 判断职位输入是否为空
		String officeStr = etOffice.getText().toString().trim();
		if(TextUtils.isEmpty(officeStr)){
			ToastUtil.showToast(mContext, mContext.getResources()
					.getString(R.string.act_expri_office_null_vcode));
			return;
		}else{
			// 职位大于2个字符
			if(officeStr.length()<2){
				ToastUtil.showToast(mContext, mContext.getResources()
						.getString(R.string.act_expri_office_err_vcode));
				return;
			}
		}
		if (mp != null) {
			mp.show();
		}
		Isexperience(phoneStr, verifyStr,companyStr,officeStr);
	}
	
	
	/**
	 * 获取验证码
	 * 
	 * @param phoneNum
	 */
	private void getVerificationCode(final String phoneNum) {
		// 发起网络请求
		ServiceProvider.getVerificationCode(mContext, phoneNum,
				new VolleyListener(mContext) {
					@Override
					public void onStart() {
						// 发起请求时调用 显示ProgressDialog
					}

					@Override
					public void onResponse(Object responseObject) {
						// 收到响应时调用
						JSONObject response = (JSONObject) responseObject;
						System.out.println("获取验证码==========>"
								+ response.toString());
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// 响应错误
						ToastUtil.showToast(mContext, R.string.error_service);
						recLen = 0;
						sendCanClickInput();
					}
				});

	}

	/**
	 * 功能描述: 快读体验接口验证
	 * @param phoneNum  电话
	 * @param vcode		验证码
	 * @param company	公司
	 * @param office	职位
	 */
	private void Isexperience(final String phoneNum, String vcode,String company,String office) {
		// 发起网络请求
		ServiceProvider.Isexperience(ExperienceActivity.this, phoneNum, vcode,company,office,
				new VolleyListener(ExperienceActivity.this) {
					@Override
					public void onStart() {
						// 发起请求时调用 显示ProgressDialog
						// if (null != mProgressDialog && mContext != null
						// && !mActivity.isFinishing()) {
						// mProgressDialog.show();
						// }
					}

					@Override
					public void onResponse(Object responseObject) {
						if (mp != null && mContext != null && mActivity != null) {
							mp.dismiss();
						}
						// 收到响应时调用
						JSONObject response = (JSONObject) responseObject;
						System.out.println("快速体验接口==========>"
								+ response.toString());

						// 验证返回码是否正确
						int code = response.optInt(Net.CODE);
						if (code==Net.LOGOUT) {
							AppApplication.logoutShow(mContext);
							return;
						}
						if (code != Net.SUCCESS) {
							ToastUtil.showToast(mContext,
									R.string.act_expri_verify_err_vcode);
							return;
						}
						JSONObject dataJson = response.optJSONObject(Net.DATA);
						int isNewUser = dataJson.optInt(ResponseParams.EXPER_IS_NEW_USER);
						SP.putIntSP(mContext, SP.DEFAULTCACHE,ResponseParams.EXPER_IS_NEW_USER,isNewUser);
						// 如果是老用户的话，就不在提醒进入首页了
						if(1 == isNewUser){
							// 返回码正确时 调用
							loginSuccess(dataJson,phoneNum);
						}else{
							// 返回码正确时 调用
							Intent webAct = new Intent(mContext, TipsWebView.class);
							webAct.putExtra(TipsWebView.VALUE_TipsWebView_JSON, response.optString(Net.DATA));
							webAct.putExtra(TipsWebView.KEY_TipsWebView, TipsWebView.ACT_Login);
							webAct.putExtra(BackWebActivity.VALUE_URL,
									"http://mworking.cn/badou/welcome.html");
							webAct.putExtra(TipsWebView.KEY_TipsWebView_PHONE,
									phoneNum);
							startActivity(webAct);
						}
						finish();
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// 响应错误
						if (mp != null && mContext != null && mActivity != null) {
							mp.dismiss();
						}
						ToastUtil.showToast(mContext,R.string.error_service);
					}
				});
	}
	

	/**
	 * 登录成功 保存信息
	 * 
	 * @param username
	 * @param password
	 * @param jsonObject
	 *            登录成功返回的json
	 */
	private void loginSuccess(JSONObject jsonObject,String acount) {
		String shuffleStr = jsonObject.optJSONObject("shuffle").toString();
		SP.putStringSP(ExperienceActivity.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, shuffleStr);
		// 验证成功 跳转到ExperienceDetailAct
		UserInfo userInfo = new UserInfo();
		/***保存没MD5的用户账户 **/
		SP.putStringSP(mContext,SP.DEFAULTCACHE, LoginActivity.KEY_ACCOUNT, acount+"");
		userInfo.setUserInfo(new MD5().getMD5ofStr(acount), jsonObject);
		// 保存用户登录成功返回的信息 到sharePreferncers
		((AppApplication) getApplicationContext()).setUserInfo(userInfo);
		Intent intent = new Intent(mContext, MainGridActivity.class);
		startActivity(intent);
	}
}
