package com.badou.mworking.base;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.widget.WaitProgressDialog;

public class BaseActionBarActivity extends BaseNoTitleActivity {

    protected View mActionBarView;
    protected LinearLayout mBaseContainer;
    protected TextView mTitleTextView;
    protected ImageView mTitleLeftImageView;
    protected FrameLayout mTitleContainerLayout;
    protected ProgressBar mTitleProgressBar;
    protected LinearLayout mTitleRightContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mBaseContainer = (LinearLayout) findViewById(R.id.activity_base_container);
        initActionBarView();
        initActionBarListener();
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(mContext).inflate(layoutResID, mBaseContainer, false);
        mBaseContainer.addView(view);
    }

    /**
     * 功能描述:初始化view
     */
    private void initActionBarView() {
        mActionBarView = findViewById(R.id.action_bar);
        mTitleTextView = (TextView) mActionBarView.findViewById(R.id.tv_actionbar_title);
        mTitleLeftImageView = (ImageView) mActionBarView.findViewById(R.id.iv_actionbar_left);
        mProgressDialog = new WaitProgressDialog(mContext);
        mTitleContainerLayout = (FrameLayout) mActionBarView.findViewById(R.id.fl_actionbar_title_container);
        mTitleProgressBar = (ProgressBar) mActionBarView.findViewById(R.id.pb_actionbar);
        mTitleRightContainer = (LinearLayout) mActionBarView.findViewById(R.id.ll_actionbar_right);
    }

    /**
     * 功能描述:设置监听
     */
    private void initActionBarListener() {
        mTitleLeftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickLeft();
            }
        });
    }

    public void setLeft(int resId) {
        if (resId < 0) {
            mTitleLeftImageView.setVisibility(View.GONE);
        } else {
            mTitleLeftImageView.setVisibility(View.VISIBLE);
            mTitleLeftImageView.setImageResource(resId);
        }
    }

    public void setActionbarTitle(String s) {
        mTitleTextView.setText(s);
    }

    public void setActionbarTitle(int resId) {
        mTitleTextView.setText(resId);
    }

    public void showActionbar() {
        mActionBarView.setVisibility(View.VISIBLE);
    }

    public void hideActionbar() {
        mActionBarView.setVisibility(View.GONE);
    }

    public void clickLeft() {
    }

    // 用tag做区分，不使用id，防止和已有id冲突
    public void addTitleRightView(View view, OnClickListener onClickListener) {
        mTitleRightContainer.addView(view);
        view.setOnClickListener(onClickListener);
    }

    public void setRightImage(int resId, OnClickListener onClickListener) {
        addTitleRightView(getDefaultImageView(mContext, resId), onClickListener);
    }

    public void setRightText(int resId, OnClickListener onClickListener) {
        addTitleRightView(getDefaultTextView(mContext, resId), onClickListener);
    }

    public void setTitleCustomView(View view) {
        mTitleContainerLayout.removeAllViews();
        mTitleContainerLayout.addView(view);
    }

    public void showProgressBar() {
        mTitleProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mTitleProgressBar.setVisibility(View.GONE);
    }

    public static ImageView getDefaultImageView(Context context, int resId) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.width_title_bar), context.getResources().getDimensionPixelSize(R.dimen.height_title_bar));
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(resId);
        return imageView;
    }

    protected static TextView getDefaultTextView(Context context, int resId) {
        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.color_text_black));
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine(true);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_size_less));
        int paddingHorizontal = context.getResources().getDimensionPixelOffset(R.dimen.offset_less);
        int paddingVertical = context.getResources().getDimensionPixelOffset(R.dimen.offset_small);
        textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        textView.setText(resId);
        return textView;
    }

    public static ImageView getDefaultLogoImageView(Context context, String url) {
        ImageView logoImage = new ImageView(context);
        logoImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, context.getResources().getDimensionPixelOffset(R.dimen.height_title_bar)));
        logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.offset_lless);
        logoImage.setPadding(padding, padding, padding, padding);
        ImageViewLoader.setImageViewResource(logoImage, R.drawable.logo, url);
        return logoImage;
    }
}
