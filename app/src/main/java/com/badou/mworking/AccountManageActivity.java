package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.AccountManagerPresenter;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 功能描述: 账号管理页面（个人中心点击设置后进入）
 */
public class AccountManageActivity extends BaseBackActionBarActivity {

    @InjectView(R.id.tv_username)
    TextView tvUsername;
    @InjectView(R.id.et_original)
    EditText etOriginal;
    @InjectView(R.id.et_new)
    EditText etNew;
    @InjectView(R.id.et_confirm)
    EditText etConfirm;
    @InjectView(R.id.btn_change_password)
    Button btnChangePassword;
    @InjectView(R.id.btn_logout)
    Button btnLogout;

    AccountManagerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_Myzhanghao));
        setContentView(R.layout.activity_account_manager);
        ButterKnife.inject(this);
        presenter = new AccountManagerPresenter();
        presenter.setAccountManageActivity(this);
        presenter.initialize();
    }

    class PasswordTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String originPassword = etOriginal.getText().toString();
            String newPassword = etNew.getText().toString();
            String confirmPassword = etConfirm.getText().toString();
            if (TextUtils.isEmpty(originPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)
                    || originPassword.length() < 6 || newPassword.length() < 6 || confirmPassword.length() < 6) {
                disableButton();
                return;
            }
            enableButton();
        }
    }

    public void disableButton() {
        btnChangePassword.setTextColor(getResources().getColor(R.color.color_text_black));
        btnChangePassword.setBackgroundResource(R.drawable.background_button_disable);
        btnChangePassword.setEnabled(false);
    }

    public void enableButton() {
        btnChangePassword.setTextColor(getResources().getColor(R.color.color_button_text_blue));
        btnChangePassword.setBackgroundResource(R.drawable.background_button_enable_blue);
        btnChangePassword.setEnabled(true);
    }

    public void anonymousMode() {
        disableButton();
        etNew.setInputType(InputType.TYPE_NULL);
        etOriginal.setInputType(InputType.TYPE_NULL);
        etConfirm.setInputType(InputType.TYPE_NULL);
        etNew.setOnClickListener(new AnonymousOnClickListener());
        etOriginal.setOnClickListener(new AnonymousOnClickListener());
        etConfirm.setOnClickListener(new AnonymousOnClickListener());
    }

    public void normalMode() {
        disableButton();
        PasswordTextWatcher passwordTextWatcher = new PasswordTextWatcher();
        etOriginal.addTextChangedListener(passwordTextWatcher);
        etNew.addTextChangedListener(passwordTextWatcher);
        etConfirm.addTextChangedListener(passwordTextWatcher);
    }

    public void setAccount(String account) {
        tvUsername.setText(account);
    }

    class AnonymousOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            ToastUtil.showToast(mContext, "你是游客，请退出登录");
        }
    }

    // 功能描述: 退出登录
    @OnClick(R.id.btn_logout)
    void logout() {
        ((AppApplication) getApplication()).clearUserInfo();
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 功能描述:  修改密码
    @OnClick(R.id.btn_change_password)
    void changePassword() {
        String originalPassword = etOriginal.getText().toString();
        String newPassword = etNew.getText().toString();
        String confirmPassword = etConfirm.getText().toString();

        Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
        boolean a = pattern.matcher(newPassword).matches();

        if (TextUtils.isEmpty(originalPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_empty_password_original);
        } else if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_empty_password_new);
        } else if (originalPassword.length() < 6) {
            ToastUtil.showToast(mContext, R.string.change_error_short_password_original);
        } else if (newPassword.length() < 6) {
            ToastUtil.showToast(mContext, R.string.change_error_short_password_new);
        } else if (!a) {
            ToastUtil.showToast(mContext, R.string.tips_username_input_New_MiMa);
        } else if (originalPassword.equals(newPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_same_new_original);
        } else if (TextUtils.isEmpty(confirmPassword) || !newPassword.equals(confirmPassword)) {
            ToastUtil.showToast(mContext, R.string.change_error_different_password);
        } else {
            mProgressDialog.setContent(R.string.change_action_change_passwrod);
            mProgressDialog.show();
            ServiceProvider.doChangePassword(mContext, originalPassword,
                    newPassword, new VolleyListener(mContext) {

                        @Override
                        public void onErrorCode(int code) {
                            ToastUtil.showToast(mContext, mActivity.getString(R.string.change_error_incorrect_password));
                        }

                        @Override
                        public void onCompleted() {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            changePasswordSuccess(response.optJSONObject(Net.DATA));
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
        UserInfo userInfo = ((AppApplication) getApplicationContext()).getUserInfo();
        userInfo.userId = data.optString(RequestParameters.USER_ID);
        userInfo.saveUserInfo(getApplicationContext());
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(userInfo.userId);
        ToastUtil.showToast(mContext, mActivity
                .getString(R.string.change_result_change_password_success));
        finish();
    }
}
