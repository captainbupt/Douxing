package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 功能描述: 账号管理页面（个人中心点击设置后进入）
 */
public class AccountManageActivity extends BaseBackActionBarActivity {

    private EditText mOriginalEditText;
    private EditText mNewEditText;
    private EditText mConfirmEditText;
    private TextView mUserNameTextView;
    private Button mLogoutButton;    //退出登录
    private Button mChangePasswordButton; // 修改密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_Myzhanghao));
        setContentView(R.layout.activity_account_manager);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mUserNameTextView = (TextView) findViewById(R.id.tv_change_password_username);
        mOriginalEditText = (EditText) findViewById(R.id.et_change_password_original);
        mNewEditText = (EditText) findViewById(R.id.et_change_password_new);
        mConfirmEditText = (EditText) findViewById(R.id.et_change_password_confirm);
        //退出登录
        mLogoutButton = (Button) findViewById(R.id.btn_logout_password_confirm);
        //修改密码
        mChangePasswordButton = (Button) findViewById(R.id.btn_change_password_confirm);
    }

    private void initListener() {
        mChangePasswordButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changePassword(mOriginalEditText.getText().toString(),
                        mNewEditText.getText().toString(),
                        mConfirmEditText.getText().toString());
            }
        });
        mLogoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void initData() {
        String account = ((AppApplication) getApplication()).getUserInfo().account;
        mUserNameTextView.setText(account);
        if (!TextUtils.isEmpty(account)) {
            if ("anonymous".equals(account)) {
                mNewEditText.setEnabled(false);
                mOriginalEditText.setEnabled(false);
                mConfirmEditText.setEnabled(false);
                mChangePasswordButton.setBackgroundColor(getResources().getColor(R.color.color_grey));
                mChangePasswordButton.setEnabled(false);
                anonymousMode();
            }
        }
    }

    private void anonymousMode() {
        mChangePasswordButton.setBackgroundResource(R.drawable.background_button_disable);
        mChangePasswordButton.setTextColor(getResources().getColor(R.color.color_text_black));
        mChangePasswordButton.setEnabled(false);
        mNewEditText.setInputType(InputType.TYPE_NULL);
        mOriginalEditText.setInputType(InputType.TYPE_NULL);
        mConfirmEditText.setInputType(InputType.TYPE_NULL);
        mNewEditText.setOnClickListener(new AnonymousOnClickListener());
        mOriginalEditText.setOnClickListener(new AnonymousOnClickListener());
        mConfirmEditText.setOnClickListener(new AnonymousOnClickListener());
    }

    class AnonymousOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            ToastUtil.showToast(mContext, "你是游客，请退出登录");
        }
    }

    /**
     * 功能描述: 退出登录
     */
    private void logout() {
        ((AppApplication) getApplication()).clearUserInfo();
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 功能描述:  修改密码
     *
     * @param originalPassword 旧密码
     * @param newPassword      新密码
     * @param confirmPassword  确认新密码
     */
    private void changePassword(String originalPassword, String newPassword,
                                String confirmPassword) {

        Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
        boolean a = pattern.matcher(newPassword).matches();

        if (TextUtils.isEmpty(originalPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_empty_password_original);
        } else if (TextUtils.isEmpty(newPassword)
                || TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_empty_password_new);
        } else if (originalPassword.length() < 6) {
            ToastUtil.showToast(mContext, R.string.change_error_short_password_original);
        } else if (newPassword.length() < 6) {
            ToastUtil.showToast(mContext, R.string.change_error_short_password_new);
        } else if (!a) {
            ToastUtil.showToast(mContext, R.string.tips_username_input_New_MiMa);
        }
        /**
         * chygt 修改于2014.6.12 判断新旧密码是否一致的问题 如果一致需要重新输入
         * **/
        else if (originalPassword.equals(newPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_same_new_original);
        } else if (TextUtils.isEmpty(confirmPassword)
                || !newPassword.equals(confirmPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_different_password);
        } else {
            mProgressDialog.setContent(R.string.change_action_change_passwrod);
            mProgressDialog.show();
            ServiceProvider.doChangePassword(mContext, originalPassword,
                    newPassword, new VolleyListener(mContext) {

                        @Override
                        public void onErrorCode(int code) {
                            ToastUtil.showToast(mContext, mActivity
                                    .getString(R.string.change_error_incorrect_password));
                        }

                        @Override
                        public void onCompleted() {
                            if (!mActivity.isFinishing()) {
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            changePasswordSuccess(response
                                    .optJSONObject(Net.DATA));
                        }
                    });
        }
    }

    /**
     * 功能描述:  修改密码
     *
     * @param data
     */
    private void changePasswordSuccess(JSONObject data) {
        UserInfo userInfo = ((AppApplication) getApplicationContext())
                .getUserInfo();
        userInfo.userId = data.optString(RequestParameters.USER_ID);
        userInfo.saveUserInfo(getApplicationContext());
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(
                userInfo.userId);
        ToastUtil.showToast(mContext, mActivity
                .getString(R.string.change_result_change_password_success));
        finish();
    }
}
