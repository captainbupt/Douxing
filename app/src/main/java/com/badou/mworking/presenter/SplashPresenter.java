package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.badou.mworking.IntroductionActivity;
import com.badou.mworking.LoginActivity;
import com.badou.mworking.MainGridActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.SplashView;

public class SplashPresenter extends Presenter {

    SplashView splashView;
    UserInfo mUserInfo;

    public SplashPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        splashView = (SplashView) v;
        initialize();
    }

    private void initialize() {
        mUserInfo = SPHelper.getUserInfo();
        String flashUrl = SPHelper.getFlashUrl();
        if (!TextUtils.isEmpty(flashUrl) && mUserInfo != null) {
            splashView.setBackgroundImage(flashUrl);
        }
        // 等待1-2秒后进入后续界面
        new Handler().postDelayed(new JumpRunnable(), 1500);

    }

    class JumpRunnable implements Runnable {

        @Override
        public void run() {
            //判断是否是第一次启动程序
            if (!SPHelper.isFirstNewVersion()) {
                //查看shareprefernces中是否保存的UserInfo(登录时保存的)
                if (mUserInfo == null) {
                    goLogin();
                } else {
                    goMain(mUserInfo);
                }
            } else {
                SPHelper.clearSP();
                //软件运行过sp中记录
                SPHelper.setIsFirstNewVersion(false);
                goIntroduction();
            }
        }
    }

    /**
     * 功能描述:跳转到登录页面
     */
    private void goLogin() {
        mContext.startActivity(LoginActivity.getIntent(mContext));
        ((Activity) mContext).finish();
    }

    /**
     * 功能描述: 跳转到主页面
     */
    private void goMain(UserInfo userInfo) {
        UserInfo.setUserInfo((AppApplication) mContext.getApplicationContext(), SPHelper.getUserAccount(), userInfo);
        mContext.startActivity(MainGridActivity.getIntent(mContext, false));
        ((Activity) mContext).finish();
    }

    /**
     * 功能描述: 跳转到引导页面
     */
    private void goIntroduction() {
        mContext.startActivity(IntroductionActivity.getIntent(mContext));
        ((Activity) mContext).finish();
    }

}
