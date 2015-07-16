package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.presenter.SplashPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.SplashView;

import cn.jpush.android.api.JPushInterface;

/**
 * 启动页面
 */
public class SplashActivity extends BaseNoTitleActivity implements SplashView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        disableSwipeBack();
        DensityUtil.init(mActivity);
        SplashPresenter splashPresenter = new SplashPresenter(mContext);
        splashPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //极光推送
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 极光推送
        JPushInterface.onResume(this);
    }
}

