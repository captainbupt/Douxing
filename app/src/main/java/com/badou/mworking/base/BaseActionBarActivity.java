package com.badou.mworking.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
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

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.R;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.widget.WaitProgressDialog;

import org.json.JSONObject;

public class BaseActionBarActivity extends BaseNoTitleActivity {

    public final static String KEY_TITLE = "title";

    protected View actionBarView;
    protected TextView mTitleTextView;
    protected ImageView mTitleLeftImageView;
    protected FrameLayout mTitleContainerLayout;
    protected ProgressBar mTitleProgressBar;
    protected LinearLayout mTitleRightContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBarView();
        initActionBarListener();
        initActionBarData();
    }

    /**
     * 功能描述:初始化view
     */
    private void initActionBarView() {
        actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar, new LinearLayout(mContext), false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarView);
        mTitleTextView = (TextView) actionBarView.findViewById(R.id.tv_actionbar_title);
        mTitleLeftImageView = (ImageView) actionBarView.findViewById(R.id.iv_actionbar_left);
        mProgressDialog = new WaitProgressDialog(mContext);
        mTitleContainerLayout = (FrameLayout) actionBarView.findViewById(R.id.fl_actionbar_title_container);
        mTitleProgressBar = (ProgressBar) actionBarView.findViewById(R.id.pb_actionbar);
        mTitleRightContainer = (LinearLayout) actionBarView.findViewById(R.id.ll_actionbar_right);
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

    private void initActionBarData() {
        mReceivedIntent = getIntent();
        if (mReceivedIntent != null) {
            String title = mReceivedIntent.getStringExtra(KEY_TITLE);
            if (!TextUtils.isEmpty(title)) {
                setActionbarTitle(title);
            }
        }
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

    protected static ImageView getDefaultImageView(Context context, int resId) {
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

    protected void addStatisticalImageView(final String sid) {
        ImageView imageView = getDefaultImageView(mContext, R.drawable.button_title_admin_statistical);
        addTitleRightView(imageView, new OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleStr = getResources().getString(R.string.statistical_data);
                String uid = UserInfo.getUserInfo().getUid();
                String url = Net.getRunHost(mContext) + Net.getTongji(uid, sid);
                Intent intent = new Intent(mContext, BackWebActivity.class);
                intent.putExtra(BackWebActivity.KEY_URL, url);
                intent.putExtra(BackWebActivity.KEY_TITLE, titleStr);
                startActivity(intent);
            }
        });
    }

    protected void addStoreImageView(boolean isStored, final String type, final String sid) {
        final ImageView imageView = getDefaultImageView(mContext, isStored ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
        imageView.setTag(isStored);
        addTitleRightView(imageView, new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isStored = (boolean) imageView.getTag();
                if (isStored) {
                    mProgressDialog.setContent(R.string.progress_tips_delete_store_ing);
                    mProgressDialog.show();
                    ServiceProvider.deleteStore(mContext, sid, type, new VolleyListener(mContext) {
                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            imageView.setImageResource(R.drawable.button_title_store_unchecked);
                            imageView.setTag(false);
                            onStoreChanged(false);
                        }

                        @Override
                        public void onCompleted() {
                            mProgressDialog.dismiss();
                        }
                    });
                } else {
                    mProgressDialog.setContent(R.string.progress_tips_store_ing);
                    mProgressDialog.show();
                    ServiceProvider.addStore(mContext, sid, type, new VolleyListener(mContext) {
                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            imageView.setImageResource(R.drawable.button_title_store_checked);
                            imageView.setTag(true);
                            onStoreChanged(true);
                        }

                        @Override
                        public void onCompleted() {
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    protected void onStoreChanged(boolean isStore) {

    }

}
