package com.badou.mworking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.ResponseError;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.MD5;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.InputMethodRelativeLayout;
import com.badou.mworking.widget.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.badou.mworking.widget.LoginErrorDialogActivity;
import com.badou.mworking.widget.WaitProgressDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 类: <code> LoginActivity </code> 功能描述: 登录页面 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午4:37:30 开发环境: JDK7.0
 */
public class LoginActivity extends BaseNoTitleActivity implements
		OnSizeChangedListenner, OnClickListener, BDLocationListener {

	private InputMethodRelativeLayout layout;
	private ViewGroup boot;
	private ViewGroup login_logo_layout_h;
	private ViewGroup login_logo_layout_v;
	private EditText mUsername; // 账号输入框
	private EditText mPassword; // 密码输入框
	private Button loginButton; // 登录按钮
	private TextView forgetBtn; // 忘记密码textview
	private TextView experienceBtn;// 快速体验
	private ProgressDialog mProgressDialog;
	public static final String KEY_ACCOUNT = "user_account";
	public static final String SHUFFLE = "shuffle";
	
	
	private String userName = "";  // 用户名
	private String passWord = "";  //　密码

	/*** 百度定位 **/
	public LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// 实例化view控件
		initView();
		initlocation();
		// 控件设置监听
		initListener();
		// 动态设置登录button的尺寸
		mProgressDialog = new WaitProgressDialog(mContext,
				R.string.login_action_login_ing);
		// 初始化数据
		initData();
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
	 * 功能描述: 初始化定位数据
	 */
	private void initlocation() {
		LocationClientOption option = new LocationClientOption();
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(this);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AppManager.getAppManager().AppExit(this, false);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * 功能描述:初始化view
	 */
	private void initView() {
		// 实例化控件
		mUsername = (EditText) findViewById(R.id.et_login_username);
		mPassword = (EditText) findViewById(R.id.et_login_password);
		loginButton = (Button) findViewById(R.id.btn_login_sign_in);
		forgetBtn = (TextView) findViewById(R.id.tv_login_forget_password);
		experienceBtn = (TextView) this.findViewById(R.id.experience);
		// 取得InputMethodRelativeLayout组件
		layout = (InputMethodRelativeLayout) this.findViewById(R.id.loginpage);
		// 设置监听事件
		layout.setOnSizeChangedListenner(this);
		// 取得大LOGO布局
		login_logo_layout_v = (ViewGroup) this
				.findViewById(R.id.login_logo_layout_v);
		// 取得小LOGO布局
		login_logo_layout_h = (ViewGroup) this
				.findViewById(R.id.login_logo_layout_h);

		// 取得找回密码和新注册布局
		boot = (ViewGroup) this
				.findViewById(R.id.reg_and_forget_password_layout);
		/**
		 * 隐藏键盘
		 */
		InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
	}

	/**
	 * 
	 * 功能描述:设置监听
	 */
	private void initListener() {

		/**
		 * Username 添加文本改变监听
		 */
		mUsername.addTextChangedListener(new TextChangeListener(mUsername,
				mPassword));
		mPassword.addTextChangedListener(new TextChangeListener(mUsername,
				mPassword));

		// 忘记密码
		forgetBtn.setOnClickListener(this);
		experienceBtn.setOnClickListener(this);
		// 登录button
		loginButton.setOnClickListener(this);

	}

	/**
	 * 功能描述: 初始化用户名
	 */
	private void initData() {
		mUsername.setText(SP.getStringSP(mContext,SP.DEFAULTCACHE, KEY_ACCOUNT, ""));
	}

	/**
	 * 
	 * 功能描述:验证用户名,密码格式是否正确
	 * 
	 * @param username
	 * @param password
	 */
	void sign_in(String username, String password, JSONObject localtion) {
		// 发起网络请求
		verify(username, password, localtion);
	}

	/**
	 * 
	 * 功能描述:用户密码格式正确时,发起网络请求传递信息
	 * 
	 * @param username
	 * @param password
	 */
	private void verify(final String username, final String password,
			JSONObject localtion) {
		// 发起网络请求
		ServiceProvider.doLogin(mContext, username, password, localtion,
				new VolleyListener(mContext) {
					@Override
					public void onStart() {

					}

					@Override
					public void onResponse(Object responseObject) {
						// 收到响应时调用
						JSONObject response = (JSONObject) responseObject;
						if (null != mProgressDialog && mContext != null
								&& !mActivity.isFinishing()) {
							mProgressDialog.dismiss();
						}
						try {
							// 验证返回码是否正确
							int code = response.optInt(Net.CODE);
							if (code == Net.LOGOUT) {
								AppApplication.logoutShow(mContext);
								return;
							}
							if (code != Net.SUCCESS) {
								showErrorDialog(R.string.login_error_incorrect_username_password);
								return;
							}
							// 返回码正确时 调用
							loginSuccess(username,
									response.optJSONObject(Net.DATA));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// 响应错误
						if (null != mProgressDialog && mContext != null
								&& !mActivity.isFinishing()) {
							mProgressDialog.dismiss();
						}
						if (error instanceof ResponseError) {
							showErrorDialog(error.getMessage());
							return;
						}
						showErrorDialog(R.string.error_service);
					}
				});
	}

	/**
	 * 
	 * 功能描述:登录失败 清空edittext并提示信息
	 * 
	 * @param tips
	 *            提示信息的String
	 */
	private void showErrorDialog(String tips) {
		Intent intent = new Intent(mContext, LoginErrorDialogActivity.class);
		intent.putExtra(LoginErrorDialogActivity.VALUE_TIPS, tips);
		startActivity(intent);
		mPassword.setText("");
	}

	/**
	 * 
	 * 功能描述:提示错误信息
	 * 
	 * @param resId
	 *            提示信息的resId
	 */
	private void showErrorDialog(int resId) {
		String tips = getResources().getString(resId);
		showErrorDialog(tips);
	}

	/**
	 * 登录成功 保存信息
	 * 
	 * @param username
	 * @param jsonObject
	 *            登录成功返回的json
	 */
	private void loginSuccess(String username, JSONObject jsonObject) {
		String shuffleStr = jsonObject.optJSONObject("shuffle").toString();
		SP.putStringSP(LoginActivity.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, shuffleStr);
		UserInfo userInfo = new UserInfo();
		/*** 保存没MD5的用户账户 **/
		SP.putStringSP(mContext,SP.DEFAULTCACHE, KEY_ACCOUNT, username + "");
		userInfo.setUserInfo(new MD5().getMD5ofStr(username), jsonObject);
		String lang = jsonObject.optString("lang");
		String company = jsonObject.optString("company");
		SP.putStringSP(this,SP.DEFAULTCACHE, Constant.COMPANY, company); //保存公司的名称
		SP.putStringSP(mContext,SP.DEFAULTCACHE, "host", jsonObject.optString("host"));
		// en为英文版，取值zh为中文版。
		changeLanguage(lang);
		try {
			SP.putStringSP(mContext,SP.DEFAULTCACHE, "host", jsonObject.optString("host"));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		// 保存用户登录成功返回的信息 到sharePreferncers
		((AppApplication) getApplicationContext()).setUserInfo(userInfo);
		goMainGrid();
	}

	/**
	 * 功能描述:跳转到主页
	 */
	private void goMainGrid() {
		Intent intent = new Intent(mContext, MainGridActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		super.finish();
	}

	/**
	 * 类: <code> TextChangeListener </code> 功能描述: 用户名 密码输入框 文本改变监听 创建人:董奇 创建日期:
	 * 2014年7月21日 下午2:03:38 开发环境: JDK7.0
	 */
	class TextChangeListener implements TextWatcher {
		private EditText editUser, editPass;

		public TextChangeListener(EditText editUser, EditText editPass) {
			this.editUser = editUser;
			this.editPass = editPass;
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		// 变化之前的内容
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			Resources res = mContext.getResources();
			if (editUser.getText().length() == 0
					|| editPass.getText().length() == 0) {
				loginButton.setEnabled(false);
				loginButton.setTextColor(res
						.getColor(R.color.color_text_qian_grey));
			} else {
				loginButton.setEnabled(true);
				loginButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * 在Activity中实现OnSizeChangedListener，原理是设置该布局的paddingTop属性来控制子View的偏移
	 */
	@Override
	public void onSizeChange(boolean flag, int w, int h) {
		if (flag) {// 键盘弹出时
			layout.setPadding(0, -10, 0, 0);
			boot.setVisibility(View.GONE);
			login_logo_layout_v.setVisibility(View.GONE);
			login_logo_layout_h.setVisibility(View.VISIBLE);
		} else { // 键盘隐藏时
			layout.setPadding(0, 0, 0, 0);
			boot.setVisibility(View.VISIBLE);
			login_logo_layout_v.setVisibility(View.VISIBLE);
			login_logo_layout_h.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_login_sign_in:
			userName = mUsername.getText().toString();
			passWord = mPassword.getText().toString();
			Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
			boolean a= pattern.matcher(userName).matches();
			boolean b= pattern.matcher(passWord).matches();
			if (!a) {
				/**
				 * true是正确的格式 
				 */
				ToastUtil.showToast(mContext, R.string.tips_username_input_CuoWu);
			}else if (!b) {
				ToastUtil.showToast(mContext, R.string.tips_username_input_MiMa);
			}else {
				mLocationClient.start();
//				 发起请求时调用 显示ProgressDialog
				if (null != mProgressDialog && mContext != null
						&& !mActivity.isFinishing()) {
					mProgressDialog.show();
				}
			}
			break;
		case R.id.tv_login_forget_password:
			if(ToastUtil.showNetExc(this)){
				return;
			}
			Intent intent1 = new Intent(mContext, ForgetPassWordActivity.class);
			startActivity(intent1);
			break;
		case R.id.experience:            
			if(ToastUtil.showNetExc(this)){
				return;
			}
			userName = "anonymous";
			passWord = "anonymous";
			//快速体验使用默认的账号和密码直接登录进入，不在采集用户名和公司等信息
			mLocationClient.start();
//			 发起请求时调用 显示ProgressDialog
			if (null != mProgressDialog && mContext != null
					&& !mActivity.isFinishing()) {
				mProgressDialog.show();
			}
//			Intent intent2 = new Intent(mContext, ExperienceActivity.class);
//			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		mLocationClient.stop();
		JSONObject locationJsonObject = new JSONObject();
		if (location == null
				|| String.valueOf(location.getLatitude()).equals(4.9E-324)
				|| String.valueOf(location.getLongitude()).equals(4.9E-324)) {
			try {
				locationJsonObject.put(RequestParams.LOCATION_LATITUDE, 0d);
				locationJsonObject.put(RequestParams.LOCATION_LONGITUDE, 0d);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// 登录button
		} else {
			try {
				locationJsonObject.put(RequestParams.LOCATION_LATITUDE,
						location.getLatitude());
				locationJsonObject.put(RequestParams.LOCATION_LONGITUDE,
						location.getLongitude());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// 登录button
		sign_in(userName, passWord,locationJsonObject);
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {

	}

	/**
	 * 功能描述: 更换语言
	 */
	private void changeLanguage(String lang) {
		Resources resources = getResources();// 获得res资源对象
		((AppApplication) getApplication()).changeAppLanguage(resources, lang);
	}

}