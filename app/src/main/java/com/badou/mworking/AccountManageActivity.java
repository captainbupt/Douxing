package com.badou.mworking;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.presenter.AccountManagerPresenter;

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
        setContentView(R.layout.activity_account_manager);
        ButterKnife.inject(this);
        initialize();
    }

    private void initialize(){
        presenter = new AccountManagerPresenter();
        presenter.setAccountManageActivity(this);
        presenter.initialize();
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
        View.OnClickListener listener = new AnonymousOnClickListener();
        etNew.setOnClickListener(listener);
        etOriginal.setOnClickListener(listener);
        etConfirm.setOnClickListener(listener);
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

    // 功能描述: 退出登录
    @OnClick(R.id.btn_logout)
    void logoutListener() {
        presenter.logout();
    }

    // 功能描述:  修改密码
    @OnClick(R.id.btn_change_password)
    void changePasswordListener() {
        String originalPassword = etOriginal.getText().toString();
        String newPassword = etNew.getText().toString();
        String confirmPassword = etConfirm.getText().toString();
        presenter.changePassword(originalPassword, newPassword, confirmPassword);
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
            presenter.passwordModified(originPassword, newPassword, confirmPassword);
        }
    }

    class AnonymousOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            presenter.anonymousClicked();
        }
    }
}
