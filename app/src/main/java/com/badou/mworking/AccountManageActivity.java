package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 功能描述: 账号管理页面（个人中心点击设置后进入）
 */
public class AccountManageActivity extends BaseBackActionBarActivity {
	
	private EditText originalEditText;
	private EditText newEditText;
	private EditText confirmEditText;
	private TextView userNameTextView;
	private ProgressDialog mProgressDialog;
	private Button btnLogout;    //退出登录
	private Button changepassWordBtn; // 修改密码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionbarTitle(mContext.getResources().getString(R.string.title_name_Myzhanghao));
		setContentView(R.layout.activity_account_manager);
		layout.attachToActivity(this);
		userNameTextView = (TextView) findViewById(R.id.tv_change_password_username);
		try {
			String account = SP.getStringSP(mContext, SP.DEFAULTCACHE,LoginActivity.KEY_ACCOUNT, "");
			userNameTextView.setText(account);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		originalEditText = (EditText) findViewById(R.id.et_change_password_original);
		newEditText = (EditText) findViewById(R.id.et_change_password_new);
		confirmEditText = (EditText) findViewById(R.id.et_change_password_confirm);
		//退出登录
		btnLogout = (Button) findViewById(R.id.btn_logout_password_confirm);
		btnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logout();
			}
		});
		//修改密码
		changepassWordBtn = (Button) findViewById(R.id.btn_change_password_confirm);
		changepassWordBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						changePassword(originalEditText.getText().toString(),
								newEditText.getText().toString(),
								confirmEditText.getText().toString());
					}
				});
		mProgressDialog = new WaitProgressDialog(mContext,
				R.string.change_action_change_passwrod);
		mContext = this;
		
		String UserName = SP.getStringSP(mContext,SP.DEFAULTCACHE, LoginActivity.KEY_ACCOUNT, "");
		if(!TextUtils.isEmpty(UserName)){
			if("anonymous".equals(UserName)){
				newEditText.setEnabled(false);
				originalEditText.setEnabled(false);
				confirmEditText.setEnabled(false);
				changepassWordBtn.setBackgroundColor(getResources().getColor(R.color.color_grey));
				changepassWordBtn.setEnabled(false);
			}
		}
		
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
	 * 功能描述: 退出登录
	 */
	private void logout() {
		((AppApplication) getApplication()).clearUserInfo();
		AppManager.getAppManager().finishAllActivity();
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		this.finish();
	}
	
	/**
	 * 功能描述:  修改密码 
	 * @param originalPassword  旧密码
	 * @param newPassword      新密码
	 * @param confirmPassword  确认新密码
	 */
	private void changePassword(String originalPassword, String newPassword,
			String confirmPassword) {
		
		Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
		boolean a= pattern.matcher(newPassword).matches();
		
		if (TextUtils.isEmpty(originalPassword)) {
			ToastUtil.showToast(mContext, R.string.change_error_empty_password_original);
		} else if (TextUtils.isEmpty(newPassword)
				|| TextUtils.isEmpty(confirmPassword)) {
			ToastUtil.showToast(mContext, R.string.change_error_empty_password_new);
		} else if (originalPassword.length() < 6) {
			ToastUtil.showToast(mContext, R.string.change_error_short_password_original);
		} else if (newPassword.length() < 6) {
			ToastUtil.showToast(mContext, R.string.change_error_short_password_new);
		}
		else if (!a) {
			ToastUtil.showToast(mContext, R.string.tips_username_input_New_MiMa);
		}
		/**
		 * chygt 修改于2014.6.12 判断新旧密码是否一致的问题 如果一致需要重新输入
		 * **/
		else if (originalPassword.equals(newPassword)) {
			ToastUtil.showToast(mContext, R.string.change_error_same_new_original);
		}

		else if (TextUtils.isEmpty(confirmPassword)
				|| !newPassword.equals(confirmPassword)) {
			ToastUtil.showToast(mContext, R.string.change_error_different_password);
		} else {
			if (null != mProgressDialog && AccountManageActivity.this != null
					&& !AccountManageActivity.this.isFinishing()) {
				mProgressDialog.show();
			}
			ServiceProvider.doChangePassword(mContext, originalPassword,
					newPassword, new VolleyListener(mContext) {

						@Override
						public void onResponse(Object responseObject) {
							JSONObject response = (JSONObject) responseObject;
							Log.v("ArountManage_badou", " 修改密码  == "+ response);
							if (null != mProgressDialog
									&& AccountManageActivity.this != null
									&& !AccountManageActivity.this
											.isFinishing()) {
								mProgressDialog.dismiss();
							}
							try {
								int code = response.optInt(Net.CODE);
								if (code==Net.LOGOUT) {
									AppApplication.logoutShow(mContext);
									return;
								}
								if (code != Net.SUCCESS) {
									ToastUtil.showToast(mContext, mActivity
											.getString(R.string.change_error_incorrect_password));
									return;
								}
								changePasswordSuccess(response
										.optJSONObject(Net.DATA));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							if (null != mProgressDialog) {
								mProgressDialog.dismiss();
							}
							super.onErrorResponse(error);
						}
					});
		}
	}

	/**
	 * 功能描述:  修改密码
	 * @param data
	 */
	private void changePasswordSuccess(JSONObject data) {
		UserInfo userInfo = ((AppApplication) getApplicationContext())
				.getUserInfo();
		userInfo.setUserId(data.optString(RequestParams.USER_ID));
		SP.putStringSP(mContext,SP.DEFAULTCACHE, UserInfo.USER_ID, data.optString(RequestParams.USER_ID));
		MTrainingDBHelper.getMTrainingDBHelper().createUserTable(
				userInfo.getUserId());
		ToastUtil.showToast(mContext, mActivity
				.getString(R.string.change_result_change_password_success));
		finish();
	}
}
