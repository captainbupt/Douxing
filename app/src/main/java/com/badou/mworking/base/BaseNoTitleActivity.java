package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.R;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.easemob.applib.controller.HXSDKHelper;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;


public class BaseNoTitleActivity extends ActionBarActivity implements SwipeBackActivityBase {

    protected Context mContext;
    protected Activity mActivity;
    protected Intent mReceivedIntent;
    protected SwipeBackActivityHelper mHelper;
    protected SwipeBackLayout mSwipeBackLayout;
    protected WaitProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        mReceivedIntent = getIntent();
        AppManager.getAppManager().addActivity(this);
        mProgressDialog = new WaitProgressDialog(mActivity);
        getSupportActionBar().hide();
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    // 常驻Activity的theme必须把windowIsTranslucent设置为false
    // 不然会出现显示桌面的异常
    public void disableSwipeBack() {
        mSwipeBackLayout.setEdgeTrackingEnabled(0);
    }

    @Override
    protected void onDestroy() {
        mProgressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {
        //将当前Activity移除掉
        AppManager.getAppManager().removeActivity(this);
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public void showToast(int resId) {
        ToastUtil.showToast(mContext, resId);
    }

    public void showToast(String message) {
        ToastUtil.showToast(mContext, message);
    }

    public void showProgressDialog(int resId) {
        mProgressDialog.setContent(resId);
        mProgressDialog.show();
    }

    public void showProgressDialog(String message) {
        mProgressDialog.setContent(message);
        mProgressDialog.show();
    }

    public void showProgressDialog() {
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    // 从SwipeBackActivity中抄过来的
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        ViewHelper.setAlpha(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), 0.5f);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}

