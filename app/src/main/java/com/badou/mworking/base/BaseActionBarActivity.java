package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.badou.mworking.R;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;

public class BaseActionBarActivity extends SherlockFragmentActivity {

    protected SwipeBackLayout layout;

    protected Context mContext;
    protected Activity mActivity;
    protected ViewGroup layout_action;

    protected TextView mTxtTitle;
    protected ImageView mImgLeft;
    protected TextView mTxtRight;
    protected ImageView mImgRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.base, null);
        AppManager.getAppManager().addActivity(this);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.actionbar, new LinearLayout(mContext), false);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(mCustomView);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //((Toolbar)(mCustomView.getParent())).setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

        layout_action = (ViewGroup) mCustomView.findViewById(R.id.layout_action_bg);

        mTxtTitle = (TextView) mCustomView.findViewById(R.id.txt_actionbar_title);
        mImgLeft = (ImageView) mCustomView.findViewById(R.id.iv_actionbar_left);
        mTxtRight = (TextView) mCustomView.findViewById(R.id.txt_actionbar_right);
        mImgRight = (ImageView) mCustomView.findViewById(R.id.iv_actionbar_right);
        mTxtRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRight();
            }
        });
        mImgRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickRight();
            }
        });
        mImgLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickLeft();
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    @Override
    public void finish() {
        //将当前Activity移除掉
        AppManager.getAppManager().removeActivity(this);
        super.finish();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    public void setLeft(int resId) {
        if (resId < 0) {
            mImgLeft.setVisibility(View.GONE);
        } else {
            mImgLeft.setVisibility(View.VISIBLE);
            mImgLeft.setImageResource(resId);
        }
    }

    public void setActionbarTitle(String s) {
        mTxtTitle.setText(s);
    }

    public TextView getTitleTV() {
        if (mTxtTitle != null) {
            return mTxtTitle;
        }
        return null;
    }

    protected void showToast(String message) {
        ToastUtil.showToast(mContext, message);
    }

    protected void showToast(int message) {
        ToastUtil.showToast(mContext, message);
    }

    public void clickLeft() {
    }

    public void clickRight() {

    }

    public void setRightText(String text) {
        mTxtRight.setText(text);
        mImgRight.setVisibility(View.GONE);
        mTxtRight.setVisibility(View.VISIBLE);
    }

    public void setRightImage(int resId) {
        mImgRight.setImageResource(resId);
        mImgRight.setVisibility(View.VISIBLE);
        mTxtRight.setVisibility(View.GONE);
    }
}
