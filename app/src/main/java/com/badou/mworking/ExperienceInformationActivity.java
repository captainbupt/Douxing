package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.presenter.ExperienceInformationPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.ExperienceInformationView;
import com.badou.mworking.view.LoginView;
import com.badou.mworking.widget.CornerRadiusButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ExperienceInformationActivity extends BaseNoTitleActivity implements ExperienceInformationView, LoginView {
    @Bind(R.id.name_edit_text)
    EditText mNameEditText;
    @Bind(R.id.phone_edit_text)
    EditText mPhoneEditText;
    @Bind(R.id.company_edit_text)
    EditText mCompanyEditText;
    @Bind(R.id.job_edit_text)
    EditText mJobEditText;
    @Bind(R.id.back_text_view)
    CornerRadiusButton mBackTextView;
    @Bind(R.id.confirm_text_view)
    CornerRadiusButton mConfirmTextView;
    @Bind(R.id.tip_text_view)
    TextView mTipTextView;

    ExperienceInformationPresenter mExperienceInformationPresenter;

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";

    public static Intent getIntent(Context context, String account, String password) {
        Intent intent = new Intent(context, ExperienceInformationActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_PASSWORD, password);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_information);
        ButterKnife.bind(this);
        initView();
        mExperienceInformationPresenter = (ExperienceInformationPresenter) mPresenter;
        mExperienceInformationPresenter.attachView(this);
    }

    public void initView() {
        mBackTextView.setDisableMode();
        mConfirmTextView.setDisableMode();
        mConfirmTextView.setEnabled(false);
        SpannableString spannableString = new SpannableString("录入您的相关信息，\n即可获得高级体验账号服务！");
        spannableString.setSpan(new ForegroundColorSpan(0xffffd800), 14, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTipTextView.setText(spannableString);
    }

    @Override
    public Presenter getPresenter() {
        String username = mReceivedIntent.getStringExtra(KEY_ACCOUNT);
        String password = mReceivedIntent.getStringExtra(KEY_PASSWORD);
        return new ExperienceInformationPresenter(mContext, username, password);
    }

    @OnTextChanged({R.id.name_edit_text, R.id.phone_edit_text, R.id.company_edit_text, R.id.job_edit_text})
    void onTextChanged(Editable editable) {
        mExperienceInformationPresenter.onTextChanged(mNameEditText.getText().toString(), mPhoneEditText.getText().toString(), mCompanyEditText.getText().toString(), mJobEditText.getText().toString());
    }

    @OnClick(R.id.confirm_text_view)
    void onConfirm() {
        mExperienceInformationPresenter.onConfirmed(mNameEditText.getText().toString(), mPhoneEditText.getText().toString(), mCompanyEditText.getText().toString(), mJobEditText.getText().toString());
    }

    @OnClick(R.id.back_text_view)
    void onBack() {
        mExperienceInformationPresenter.onBack();
    }

    @OnClick(R.id.cancel_text_view)
    void onCancel() {
        mExperienceInformationPresenter.onCancel();
    }

    @Override
    public void onBackPressed() {
        mExperienceInformationPresenter.onBack();
    }

    @Override
    public void enableConfirmButton() {
        mConfirmTextView.setEnableMode();
        mConfirmTextView.setEnabled(true);
    }

    @Override
    public void disableConfirmButton() {
        mConfirmTextView.setDisableMode();
        mConfirmTextView.setEnabled(false);
    }

    @Override
    public void setAccount(String account) {

    }

    @Override
    public void enableLoginButton() {

    }

    @Override
    public void disableLoginButton() {

    }

    @Override
    public void showNormalLayout() {

    }

    @Override
    public void showSmallLayout() {

    }

    @Override
    public void showErrorDialog() {

    }
}
