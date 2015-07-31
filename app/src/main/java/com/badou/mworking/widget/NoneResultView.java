package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;

/**
 * Created by Administrator on 2015/6/16.
 */
public class NoneResultView extends LinearLayout {

    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView;

    public NoneResultView(Context context) {
        super(context);
        initialize(context);
    }

    public NoneResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        initAttr(context, attrs);
    }

    private void initialize(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_none_result, this, true);
        mImageView = (ImageView) findViewById(R.id.iv_view_none_result);
        mTextView = (TextView) findViewById(R.id.tv_view_none_result);
    }

    public void initAttr(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.NoneResultView);
            int imgResId = typedArray.getResourceId(
                    R.styleable.NoneResultView_imgSrc, -1);
            int textResId = typedArray.getResourceId(
                    R.styleable.NoneResultView_tipText, -1);
            typedArray.recycle();
            setContent(imgResId, textResId);
        }
    }

    public void setImageResource(int imgResId) {
        if (imgResId == -1) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageResource(imgResId);
        }
    }

    public void setTextResource(int textResId) {
        if (textResId == -1) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(textResId);
        }
    }

    public void setContent(int imgResId, int resultResId) {
        setImageResource(imgResId);
        setTextResource(resultResId);
    }

    public void setContent(int imgResId, String result) {
        setImageResource(imgResId);
        mTextView.setText(result);
    }
}
