package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.presenter.LoginPresenter;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.view.LoginView;
import com.badou.mworking.widget.InputMethodRelativeLayout;
import com.badou.mworking.widget.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.badou.mworking.widget.LoginErrorDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 功能描述: 登录页面
 */
public class LoginActivity extends BaseNoTitleActivity implements LoginView, OnSizeChangedListenner {

    @Bind(R.id.normal_content_container)
    LinearLayout mNormalContentContainer;
    @Bind(R.id.small_content_container)
    LinearLayout mSmallContentContainer;
    @Bind(R.id.username_edit_text)
    EditText mUsernameEditText;
    @Bind(R.id.password_edit_text)
    EditText mPasswordEditText;
    @Bind(R.id.login_button)
    Button mLoginButton;
    @Bind(R.id.bottom_container)
    RelativeLayout mBottomContainer;
    @Bind(R.id.base_container)
    InputMethodRelativeLayout mBaseContainer;

    LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    public static Intent getIntent(Context context) {
        UserInfo.clearUserInfo((AppApplication) context.getApplicationContext());
        return new Intent(context, LoginActivity.class);
    }

    protected void initView() {
        mLoginPresenter = new LoginPresenter(mContext);
        mLoginPresenter.attachView(this);
        // 设置监听事件
        mBaseContainer.setOnSizeChangedListenner(this);
        disableSwipeBack();
    }

    // 功能描述: 用户名 密码输入框 文本改变监听
    @OnTextChanged(value = {R.id.username_edit_text, R.id.password_edit_text})
    void onTextChanged() {
        mLoginPresenter.onTextChanged(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    @Override
    public void setAccount(String account) {
        mUsernameEditText.setText(account);
    }

    @OnClick(R.id.login_button)
    void login() {
        mLoginPresenter.login(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
    }

    @OnClick(R.id.experience_text_view)
    void experience() {
        mLoginPresenter.experience();
    }

    @OnClick(R.id.forget_text_view)
    void forget() {
        mLoginPresenter.forgetPassword();
    }

    @Override
    public void onSizeChange(boolean paramBoolean, int w, int h) {
        mLoginPresenter.onKeyboardStateChanged(paramBoolean);
    }

    @Override
    public void enableLoginButton() {
        mLoginButton.setEnabled(true);
        mLoginButton.setTextColor(getResources().getColorStateList(R.color.color_button_text_blue));
        mLoginButton.setBackgroundResource(R.drawable.background_button_enable_blue);
    }

    @Override
    public void disableLoginButton() {
        mLoginButton.setEnabled(false);
        mLoginButton.setTextColor(getResources().getColor(R.color.color_white));
        mLoginButton.setBackgroundResource(R.drawable.background_button_disable);
    }

    @Override
    public void showNormalLayout() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsernameEditText.getWindowToken(), 0);

        int padding = getResources().getDimensionPixelOffset(R.dimen.login_margin);
        mBaseContainer.setPadding(padding, padding, padding, padding);
        mBottomContainer.setVisibility(View.VISIBLE);
        mNormalContentContainer.setVisibility(View.VISIBLE);
        mSmallContentContainer.setVisibility(View.GONE);
    }

    @Override
    public void showSmallLayout() {
        int padding = getResources().getDimensionPixelOffset(R.dimen.login_margin);
        mBaseContainer.setPadding(padding, padding, padding, padding);
        mBottomContainer.setVisibility(View.GONE);
        mNormalContentContainer.setVisibility(View.GONE);
        mSmallContentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorDialog() {
        LoginErrorDialog dialog = new LoginErrorDialog(mContext, getString(R.string.login_error_incorrect_username_password));
        dialog.show();
        mPasswordEditText.setText("");
    }
}