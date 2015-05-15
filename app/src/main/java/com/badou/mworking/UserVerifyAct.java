package com.badou.mworking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.MD5;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 用户验证身份页(修改密码页)
 * @author 
 */
public class UserVerifyAct extends BaseBackActionBarActivity implements OnClickListener{

	private static final String LOG = "UserVerifyAct_";
	private EditText etPass,etVerPass;
	private TextView tvOK;
	private ProgressDialog mp;
	private Intent intent;
	public static final String VERIFY_PHONE = "UserVerify_phone";
	public static final String VERIFY_VCODE = "UserVerify_code";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_verify);
		setActionbarTitle(mContext.getResources().getString(R.string.title_name_ShenFenRenZheng));
		intent = getIntent();
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
	
	private void initView(){
		etPass = (EditText) this.findViewById(R.id.et_input_password);
		etVerPass = (EditText) this.findViewById(R.id.et_Verify_password);
		tvOK = (TextView) this.findViewById(R.id.btn_ok);
		mp = new WaitProgressDialog(mContext, R.string.exam_action_submit_ing);
		tvOK.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_ok:
			String p1 = etPass.getText().toString();
			String p2 = etVerPass.getText().toString();
			String phone = intent.getStringExtra(VERIFY_PHONE);
			String vcode = intent.getStringExtra(VERIFY_VCODE);
			Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
			boolean a= pattern.matcher(p1).matches();
			boolean b= pattern.matcher(p2).matches();
			if (TextUtils.isEmpty(p1)||TextUtils.isEmpty(p2)) {
				ToastUtil.showToast(mContext, R.string.login_error_empty_password);
			}else if (!p1.equals(p2)) {
				ToastUtil.showToast(mContext, R.string.change_error_different_password);
			}else if (p1.length() < 6) {
				showToast(R.string.change_error_short_password_original);
			}else if (p2.length() < 6) {
				showToast(R.string.change_error_short_password_new);
			}
			else if (!a) {
				ToastUtil.showToast(mContext, R.string.tips_username_input_MiMa);
			}else if (!b) {
				ToastUtil.showToast(mContext, R.string.tips_username_input_MiMa);
			}
			
			else if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(vcode)) {
				showToast(R.string.act_expri_verify_err_vcode);
			}else {
//				Log.v(LOG+"badou", " ForgetPass   跳转传过来的值--- 电话 -->> " + intent.getStringExtra(VERIFY_PHONE) +  "   ****      验证码 --->> "+ intent.getStringExtra(VERIFY_VCODE));
				ChangePass(phone, vcode, p1);
			}
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * 网络请求 忘记密码
	 * @param phoneNum
	 * @param vcode
	 * @param newpwd
	 */
	private void ChangePass(final String phoneNum,String vcode,String newpwd ) {
		if (mp!=null && mContext != null && mActivity != null) {
			mp.show();
		}
		
		ServiceProvider.doForgetPassword(mContext, phoneNum, vcode, newpwd, new VolleyListener(mContext) {
			
			@Override
			public void onResponse(Object responseObject) {
				if (mp!=null && mContext != null && mActivity != null) {
					mp.dismiss();
				}
				JSONObject response = (JSONObject) responseObject;
				try {
					// 验证返回码是否正确
					
					int code = response.optInt(Net.CODE);
					if (code==Net.LOGOUT) {
						AppApplication.logoutShow(mContext);
						return;
					}
					if (code != Net.SUCCESS) {
						ToastUtil.showToast(mContext,R.string.login_error_incorrect_username_password);
						return;
					}
					
					// 返回码正确时 调用
					chaSuccess(phoneNum,
							response.optJSONObject(Net.DATA));
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mp!=null && mContext != null && mActivity != null) {
					mp.dismiss();
				}
				ToastUtil.showToast(mContext,R.string.error_service);
			}
		});
	}
	
	private void chaSuccess(String phone,JSONObject json){
		ToastUtil.showToast(mContext, R.string.password_success);
		loginSuccess(phone, json);
	}

	/**
	 * 登录成功 保存信息
	 * 
	 * @param username
	 * @param password
	 * @param jsonObject
	 *            登录成功返回的json
	 */
	private void loginSuccess(String acount,
			JSONObject jsonObject) {
		String shuffleStr = jsonObject.optJSONObject("shuffle").toString();
		SP.putStringSP(UserVerifyAct.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, shuffleStr);
		UserInfo userInfo = new UserInfo();
		SP.putStringSP(mContext,SP.DEFAULTCACHE, LoginActivity.KEY_ACCOUNT, acount+"");
		userInfo.setUserInfo(new MD5().getMD5ofStr(acount), jsonObject);
		// 保存用户登录成功返回的信息 到sharePreferncers
		((AppApplication) getApplicationContext()).setUserInfo(userInfo);
		goMainGrid();
	}

	/**
	 * 
	 * 功能描述:跳转到主页
	 */
	private void goMainGrid() {
		Intent intent = new Intent(mContext, MainGridActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
}
