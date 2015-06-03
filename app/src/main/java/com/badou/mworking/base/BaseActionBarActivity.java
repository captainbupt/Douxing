package com.badou.mworking.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.widget.WaitProgressDialog;

public class BaseActionBarActivity extends BaseNoTitleActivity {

    public final static String KEY_TITLE = "title";

    protected View actionBarView;
    protected TextView mTitleTextView;
    protected ImageView mTitleLeftImageView;
    protected ImageView mTitleRightImageView;

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
    private void initActionBarView(){
        actionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar, new LinearLayout(mContext), false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarView);
        mTitleTextView = (TextView) actionBarView.findViewById(R.id.txt_actionbar_title);
        mTitleLeftImageView = (ImageView) actionBarView.findViewById(R.id.iv_actionbar_left);
        mTitleRightImageView = (ImageView) actionBarView.findViewById(R.id.iv_actionbar_right);
        mProgressDialog = new WaitProgressDialog(mContext);
    }

    /**
     * 功能描述:设置监听
     */
    private void initActionBarListener(){
        mTitleRightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickRight();
            }
        });
        mTitleLeftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickLeft();
            }
        });
    }

    private void initActionBarData(){
        mReceivedIntent = getIntent();
        if(mReceivedIntent != null){
            String title = mReceivedIntent.getStringExtra(KEY_TITLE);
            if(!TextUtils.isEmpty(title)){
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

    public void clickLeft() {
    }

    public void clickRight() {

    }

    public void setRightImage(int resId) {
        mTitleRightImageView.setImageResource(resId);
        mTitleRightImageView.setVisibility(View.VISIBLE);
    }
}
