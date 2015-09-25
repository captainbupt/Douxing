package com.badou.mworking.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.util.DensityUtil;

import java.text.SimpleDateFormat;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class BottomTimingView extends RelativeLayout {
    @Bind(R.id.current_time_text_view)
    TextView mCurrentTimeTextView;
    @Bind(R.id.total_time_text_view)
    TextView mTotalTimeTextView;
    @Bind(R.id.circle_progress_view)
    CircleProgressView mCircleProgressView;

    private int mMaxTime;

    public BottomTimingView(Context context) {
        super(context);
        init(context);
    }

    public BottomTimingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_timing, this, true);
        ButterKnife.bind(this);
        mCircleProgressView.setTextSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.7f));
        mCircleProgressView.setUnitSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.7f));
    }

    public void setTotalTime(int minute) {
        mTotalTimeTextView.setText(String.format("学时:%d分钟", minute));
        mMaxTime = minute * 60;
        mCircleProgressView.setMaxValue(mMaxTime);
    }

    public void setCurrentTime(int totalSecond) {
        if (totalSecond >= mMaxTime) {
            mCircleProgressView.setValue(mMaxTime);
            SpannableString spannableString = new SpannableString("学习进度:已完成");
            spannableString.setSpan(new ForegroundColorSpan(0xfff79355), 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mCurrentTimeTextView.setText(spannableString);
        } else {
            int minute = totalSecond / 60;
            int second = totalSecond % 60;
            mCircleProgressView.setValue(totalSecond);
            SpannableString spannableString = new SpannableString(String.format("学习进度:%d分%d秒", minute, second));
            spannableString.setSpan(new ForegroundColorSpan(0xfff79355), 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mCurrentTimeTextView.setText(spannableString);
        }
    }
}
