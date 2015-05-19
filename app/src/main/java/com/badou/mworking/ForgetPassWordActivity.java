package com.badou.mworking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 类: <code> ForgetPassWordActivity </code> 功能描述: 忘记密码 创建人: 葛建锋 创建日期: 2014年8月8日
 * 下午4:36:59 开发环境: JDK7.0
 */
public class ForgetPassWordActivity extends BaseBackActionBarActivity implements
		OnClickListener {

	private EditText etPhone;
	private EditText etVerify;
	private EditText etCompany;  //公司
	private EditText etOffice;  //公司
	private TextView btnOK; // 下一步
	private TextView btnGetMsg;
	private TextView tvWuFaHuoQu; // 长时间未收到短信请点这里
	private TextView tvTiShi;
	private static final int totalTime = 120;
	private int recLen = totalTime;
	
	private SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tiyan);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		setActionbarTitle(mContext.getResources().getString(
				R.string.title_name_ShenFenRenZheng));
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
	 * 功能描述:初始化控件
	 *
	 */
	protected void initView() {
		super.initView();

		etPhone = (EditText) this.findViewById(R.id.et_input_phone);
		etVerify = (EditText) this.findViewById(R.id.et_Verify);
		etCompany = (EditText) findViewById(R.id.et_company);
		etOffice = (EditText) findViewById(R.id.et_office);
		etCompany.setVisibility(View.GONE);
		etOffice.setVisibility(View.GONE);
		btnGetMsg = (TextView) this.findViewById(R.id.btn_get_duanXin);
		btnOK = (TextView) this.findViewById(R.id.btn_ok);
		tvWuFaHuoQu = (TextView) this.findViewById(R.id.tv_weiShouDao);
		tvTiShi = (TextView) this.findViewById(R.id.tv_tishi);
		String tishi = getResources().getString(
				R.string.act_forget_verify_top_tishi_1);

		int start = tishi.indexOf("400-8233-773");
		int end = start + "400-8233-773".length();

		SpannableStringBuilder style = new SpannableStringBuilder(tishi);
		style.setSpan(new URLSpanNoUnderline("400-8233-773"), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvTiShi.setText(style);
		tvTiShi.setMovementMethod(LinkMovementMethod.getInstance());
		btnGetMsg.setText(R.string.TiYan_btn_getCode);
		sendBtnCannotClick();
	}

	//未指定位置添加点击事件， 捕获到电话，然后直接拨打
	public class URLSpanNoUnderline extends ClickableSpan {
		private final String mURL;

		public URLSpanNoUnderline(String url) {
			mURL = url;
		}

		public String getURL() {
			return mURL;
		}

		@Override
		public void onClick(View widget) {
			Intent phoneIntent = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + "4008233773"));
			startActivity(phoneIntent);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(true); // 取消下划线
			ds.setColor(0xff0066ff); // 指定文字颜色
		}
	}

	/**
	 * 功能描述: 设置控件的监听
	 */
	protected void initListener() {
		super.initListener();
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
				int len = etPhone.getText().toString().length();
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
					recLen = totalTime;
					return;
				} else {
					sendCannotClickInput();
					btnGetMsg.setText(recLen
							+ mContext.getResources().getString(
									R.string.TiYan_btn_RegetCode));
					handler.postDelayed(this, 1000);
				}
			} else {
				recLen = totalTime;
			}
		}
	};

	private void sendCanClickInput() {
		int bottom = btnGetMsg.getPaddingBottom();
		int top = btnGetMsg.getPaddingTop();
		int right = btnGetMsg.getPaddingRight();
		int left = btnGetMsg.getPaddingLeft();
		btnGetMsg.setBackgroundResource(R.drawable.btn_bg_blue);
		btnGetMsg.setPadding(left, top, right, bottom);
		btnGetMsg.setEnabled(true);
		etPhone.setEnabled(true);
		btnGetMsg.setText(R.string.TiYan_btn_getCode);

	}

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
		String str = etPhone.getText().toString().trim();
		// 点击监听事件
		switch (arg0.getId()) {
		case R.id.btn_get_duanXin:// 获取验证码
			if (etPhone.getText().toString().trim().equals("")) {
				ToastUtil.showToast(mContext,
						R.string.act_yanZheng_phone_notnull);
			} else {
				sendCannotClickInput();
				handler.postDelayed(runnable, 1000);
				getVerificationCode(str);
			}
			break;
		case R.id.btn_ok:// 验证
			if (etPhone.getText().toString().trim().equals("")) {
				ToastUtil.showToast(mContext,
						R.string.act_yanZheng_phone_notnull);
			} else if (etVerify.getText().toString().trim().equals("")) {
				ToastUtil.showToast(mContext,
						R.string.act_yanZheng_verfiy_notnull);
			} else {
				go2UserAct();
				// 跳转到ExperienceDetailAct
				// Isexperience(etPhone.getText().toString().trim(),etVerify.getText().toString().trim());
			}
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
						Log.d("ForgetPassWordActivity_badou",
								"忘记密码的获取验证码 json == " + responseObject);
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						recLen = 0;
						sendCanClickInput();
						// 响应错误
						ToastUtil.showToast(mContext, R.string.error_service);
					}
				});
	}

	public void go2UserAct() {
		Intent intent = new Intent(mContext, UserVerifyAct.class);
		intent.putExtra(UserVerifyAct.VERIFY_PHONE, etPhone.getText()
				.toString());
		intent.putExtra(UserVerifyAct.VERIFY_VCODE, etVerify.getText()
				.toString());
		startActivity(intent);
		this.finish();
	}
}
