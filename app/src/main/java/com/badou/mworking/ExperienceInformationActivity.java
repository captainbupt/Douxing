package com.badou.mworking;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ExperienceInformationActivity extends BaseNoTitleActivity implements ExperienceInformationView {
    @Bind(R.id.name_edit_text)
    EditText mNameEditText;
    @Bind(R.id.phone_edit_text)
    EditText mPhoneEditText;
    @Bind(R.id.company_edit_text)
    EditText mCompanyEditText;
    @Bind(R.id.job_edit_text)
    EditText mJobEditText;
    @Bind(R.id.confirm_text_view)
    TextView mConfirmTextView;
    @Bind(R.id.tip_text_view)
    TextView mTipTextView;

    ExperienceInformationPresenter mExperienceInformationPresenter;

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
        SpannableString spannableString = new SpannableString("录入您的相关信息，\n即可获得高级体验账号服务！");
        spannableString.setSpan(new ForegroundColorSpan(0xffffd800), 14, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTipTextView.setText(spannableString);
    }

    @Override
    public Presenter getPresenter() {
        return new ExperienceInformationPresenter(mContext);
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
        mConfirmTextView.setTextColor(getResources().getColorStateList(R.color.color_button_text_blue));
        mConfirmTextView.setBackgroundResource(R.drawable.background_button_enable_blue);
        mConfirmTextView.setEnabled(true);
    }

    @Override
    public void disableConfirmButton() {
        mConfirmTextView.setTextColor(getResources().getColor(R.color.color_white));
        mConfirmTextView.setBackgroundResource(R.drawable.background_button_disable);
        mConfirmTextView.setEnabled(false);
    }
}
