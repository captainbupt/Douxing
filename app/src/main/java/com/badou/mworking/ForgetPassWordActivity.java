package com.badou.mworking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;

/**
 * 功能描述: 忘记密码
 */
public class ForgetPassWordActivity extends BaseBackActionBarActivity {

    private EditText mPhoneEditText;
    private EditText mCodeEditText;
    private TextView mNextTextView; // 下一步
    private TextView mGetCodeTextView;
    private TextView mCannotGetCodeTextView; // 长时间未收到短信请点这里
    private TextView mTopTipTextView;
    private static final int totalTime = 120;
    private int recLen = totalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setActionbarTitle(mContext.getResources().getString(
                R.string.title_name_ShenFenRenZheng));
        initView();
        initListener();
    }

    /**
     * 功能描述:初始化控件
     */
    protected void initView() {
        mPhoneEditText = (EditText) this.findViewById(R.id.et_forget_passwrod_phone);
        mCodeEditText = (EditText) this.findViewById(R.id.et_forget_password_code);
        mGetCodeTextView = (TextView) this.findViewById(R.id.tv_forget_password_get_code);
        mNextTextView = (TextView) this.findViewById(R.id.tv_forget_password_next_step);
        mCannotGetCodeTextView = (TextView) this.findViewById(R.id.tv_forget_password_cannot_receive);
        mTopTipTextView = (TextView) this.findViewById(R.id.tv_forget_password_tips);
        String tip = getResources().getString(
                R.string.act_forget_verify_top_tip);

        int start = tip.indexOf("400-8233-773");
        int end = start + "400-8233-773".length();

        SpannableStringBuilder style = new SpannableStringBuilder(tip);
        style.setSpan(new URLSpanNoUnderline("400-8233-773"), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTopTipTextView.setText(style);
        mTopTipTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mGetCodeTextView.setText(R.string.forget_password_get_code);
        setButtonDisable(mGetCodeTextView);
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
            ds.setColor(getResources().getColor(R.color.color_blue)); // 指定文字颜色
        }
    }

    /**
     * 功能描述: 设置控件的监听
     */
    protected void initListener() {
        mGetCodeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mPhoneEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showToast(mContext,
                            R.string.act_yanZheng_phone_notnull);
                } else {
                    mPhoneEditText.setEnabled(false);
                    setButtonDisable(mGetCodeTextView);
                    handler.postDelayed(runnable, 1000);
                    getVerificationCode(phone);
                }
            }
        });
        mNextTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mPhoneEditText.getText().toString().trim();
                String code = mCodeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phone.trim())) {
                    ToastUtil.showToast(mContext,
                            R.string.act_yanZheng_phone_notnull);
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.showToast(mContext,
                            R.string.act_yanZheng_verfiy_notnull);
                } else {
                    goVerification();
                }
            }
        });
        mCannotGetCodeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TipsWebView.class);
                intent.putExtra(BackWebActivity.KEY_URL,
                        "http://mworking.cn/badou/verify-help.html");
                intent.putExtra(TipsWebView.KEY_TITLE,getResources().getString(R.string.notGetMsg));
                startActivity(intent);
            }
        });

        mPhoneEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int len = editable.toString().length();
                if (len < 11) {
                    setButtonDisable(mGetCodeTextView);
                } else {
                    setButtonEnable(mGetCodeTextView);
                }
            }
        });

        mCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int len = editable.toString().length();
                if (len < 6) {
                    setButtonDisable(mNextTextView);
                } else {
                    setButtonEnable(mNextTextView);
                }
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen--;
            if (recLen >= 0) {
                if (recLen == 0) {
                    mGetCodeTextView.setText(R.string.forget_password_get_code);
                    setButtonEnable(mGetCodeTextView);
                    recLen = totalTime;
                    return;
                } else {
                    setButtonDisable(mGetCodeTextView);
                    mGetCodeTextView.setText(recLen
                            + getResources().getString(
                            R.string.forget_passwrod_get_code_unit));
                    handler.postDelayed(this, 1000);
                }
            } else {
                recLen = totalTime;
            }
        }
    };

    /**
     * 可以点击
     */
    private void setButtonDisable(TextView textView) {
        textView.setEnabled(false);
        textView.setBackgroundResource(R.drawable.background_button_disable);
        textView.setTextColor(getResources().getColor(R.color.color_white));
    }

    private void setButtonEnable(TextView textView) {
        textView.setEnabled(true);
        if (textView.getId() == R.id.tv_forget_password_get_code) {
            textView.setTextColor(getResources().getColorStateList(R.color.color_button_text_blue));
            textView.setBackgroundResource(R.drawable.background_button_enable_blue);
        } else {
            textView.setTextColor(getResources().getColorStateList(R.color.color_button_text_red));
            textView.setBackgroundResource(R.drawable.background_button_enable_red);
        }
    }

    private void getVerificationCode(final String phoneNum) {
        // 发起网络请求
        ServiceProvider.getVerificationCode(mContext, phoneNum,
                new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object responseObject) {
                        // 收到响应时调用
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        recLen = 0;
                        setButtonEnable(mGetCodeTextView);
                        // 响应错误
                        ToastUtil.showToast(mContext, R.string.error_service);
                    }
                });
    }

    public void goVerification() {
        Intent intent = new Intent(mContext, ForgetPasswordVerificationActivity.class);
        intent.putExtra(ForgetPasswordVerificationActivity.VERIFY_PHONE, mPhoneEditText.getText()
                .toString());
        intent.putExtra(ForgetPasswordVerificationActivity.VERIFY_VCODE, mCodeEditText.getText()
                .toString());
        startActivity(intent);
        this.finish();
    }
}
