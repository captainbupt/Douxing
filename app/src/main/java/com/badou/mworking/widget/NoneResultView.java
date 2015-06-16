package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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

    public NoneResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_none_result, this, true);
        mImageView = (ImageView) findViewById(R.id.iv_view_none_result);
        mTextView = (TextView) findViewById(R.id.tv_view_none_result);
    }

    public void setImageResource(int imgResId) {
        if (imgResId == -1) {
            mImageView.setVisibility(View.GONE);
        } else {
            mImageView.setImageResource(imgResId);
        }
    }

    public void setContent(int imgResId, int resultResId) {
        setImageResource(imgResId);
        mTextView.setText(resultResId);
    }
}
