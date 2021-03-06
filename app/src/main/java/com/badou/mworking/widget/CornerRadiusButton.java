package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.util.DensityUtil;

public class CornerRadiusButton extends TextView {

    Context mContext;

    GradientDrawable mPressedDrawable = new GradientDrawable();
    GradientDrawable mNormalDrawable = new GradientDrawable();
    GradientDrawable mDisableDrawable = new GradientDrawable();
    int mTextColor;
    int mBackgroundColor;

    private boolean isEnable;

    public CornerRadiusButton(Context context) {
        super(context);
        this.mContext = context;
        initAttr(mContext, null);
    }

    public CornerRadiusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttr(mContext, attrs);
    }

    public void initAttr(Context context, AttributeSet attrs) {
        mTextColor = getCurrentTextColor();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerRadiusButton);
            mBackgroundColor = typedArray.getColor(R.styleable.CornerRadiusButton_backgroundColor, context.getResources().getColor(R.color.color_blue));
            typedArray.recycle();
        } else {
            mBackgroundColor = context.getResources().getColor(R.color.color_blue);
        }

        mPressedDrawable.setShape(GradientDrawable.RECTANGLE);
        mPressedDrawable.setStroke(DensityUtil.dip2px(mContext, 1), mBackgroundColor);
        mPressedDrawable.setCornerRadius(mContext.getResources().getDimension(R.dimen.radius_small));
        mPressedDrawable.setColor(0xffffffff);

        mNormalDrawable.setShape(GradientDrawable.RECTANGLE);
        mNormalDrawable.setCornerRadius(mContext.getResources().getDimension(R.dimen.radius_small));
        mNormalDrawable.setColor(mBackgroundColor);

        mDisableDrawable.setShape(GradientDrawable.RECTANGLE);
        mDisableDrawable.setCornerRadius(mContext.getResources().getDimension(R.dimen.radius_small));
        mDisableDrawable.setColor(context.getResources().getColor(R.color.color_grey));

        onRelease();
    }

    public void onPressed() {
        setBackground(mPressedDrawable);
        setTextColor(mBackgroundColor);
    }

    public void onRelease() {
        setBackground(mNormalDrawable);
        setTextColor(mTextColor);
    }

    public void setEnableMode() {
        onRelease();
        isEnable = true;
    }

    public void setDisableMode() {
        setBackground(mDisableDrawable);
        setTextColor(0xffffffff);
        isEnable = false;
    }

    public void setBackground(Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackgroundDrawable(drawable);
        } else {
            super.setBackground(drawable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnable) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onPressed();
            } else {
                onRelease();
            }
        }
        return super.onTouchEvent(event);
    }
}
