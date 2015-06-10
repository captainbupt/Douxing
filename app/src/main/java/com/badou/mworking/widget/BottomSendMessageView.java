package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.R;

import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;

/**
 * Created by Administrator on 2015/6/3.
 */
public class BottomSendMessageView extends LinearLayout {

    private Context mContext;
    private EditText mContentEditText; // 评论输入框
    private TextView mSubmitTextView; // 评论提交按钮

    private OnSubmitListener mOnSubmitListener;

    private int mMinText;
    private int mMaxText;

    private InputMethodManager imm;

    public BottomSendMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_bottom_send_message, this);
        initView(attrs);
        initListener();
    }

    private void initView(AttributeSet attrs) {
        // 隐藏输入法
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mContentEditText = (EditText) findViewById(R.id.et_view_bottom_send_message_content);
        mSubmitTextView = (TextView) findViewById(R.id.tv_view_bottom_send_message_submit);
        mSubmitTextView.setEnabled(false);
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs,
                    R.styleable.BottomSendMessageView);
            mContentEditText.setHint(typedArray.getResourceId(R.styleable.BottomSendMessageView_contentHint, R.string.comment_hint));
            mSubmitTextView.setText(typedArray.getResourceId(R.styleable.BottomSendMessageView_submitText, R.string.feekback_submit));
            mMinText = typedArray.getInt(R.styleable.BottomSendMessageView_minText, 1);
            mMaxText = typedArray.getInt(R.styleable.BottomSendMessageView_maxText, 79);
        }
    }

    private void initListener() {
        // 字符长度监听
        mContentEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (dstart > mMaxText)
                    return "";
                return null;
            }
        }});

        // 字符改变监听
        mContentEditText.addTextChangedListener(new TextWatcher() {

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
                // 文本改变监听
                int length = mContentEditText.getText().toString().trim().length();

                if (length < mMinText) {
                    mSubmitTextView.setEnabled(false);
                    mSubmitTextView.setBackgroundResource(R.drawable.background_button_disable);
                } else {
                    mSubmitTextView.setEnabled(true);
                    mSubmitTextView.setBackgroundResource(R.drawable.background_button_enable_blue);
                }

            }
        });

        mSubmitTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String content = mContentEditText.getText().toString();
                content = content.replaceAll("\\n", "");
                // 显示或者隐藏输入法
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                if (mOnSubmitListener != null) {
                    mOnSubmitListener.onSubmit(content);
                }
                mContentEditText.setText("");
            }
        });
    }

    public interface OnSubmitListener {
        void onSubmit(String content);
    }

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
        this.mOnSubmitListener = onSubmitListener;
    }

    public void setContent(String hint, String submitText) {
        this.mContentEditText.setHint(hint);
        this.mSubmitTextView.setText(submitText);
    }

    public void clearContent() {
        this.mContentEditText.setText("");
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        imm.showSoftInput(mContentEditText, 0);
    }

}
