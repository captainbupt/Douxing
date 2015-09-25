package com.badou.mworking;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.presenter.SplashPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.view.SplashView;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

/**
 * 启动页面
 */
public class SplashActivity extends BaseNoTitleActivity implements SplashView {

    @Bind(R.id.background_image_view)
    SimpleDraweeView mBackgroundImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
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

    @Override
    public void setBackgroundImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            mBackgroundImageView.setImageURI(UriUtil.getHttpUri(url));
        }
    }
}

