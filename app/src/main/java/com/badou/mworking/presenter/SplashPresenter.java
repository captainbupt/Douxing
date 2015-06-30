package com.badou.mworking.presenter;

import android.content.Context;
import android.os.Handler;

import com.badou.mworking.IntroductionActivity;
import com.badou.mworking.LoginActivity;
import com.badou.mworking.MainGridActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.view.SplashView;
import com.badou.mworking.view.BaseView;

public class SplashPresenter extends Presenter {

    SplashView splashView;

    public SplashPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        splashView = (SplashView) v;
        initialize();
    }

    private void initialize() {
        // 等待1-2秒后进入后续界面
        new Handler().postDelayed(new JumpRunnable(), 1500);

    }

    class JumpRunnable implements Runnable {

        @Override
        public void run() {
            //判断是否是第一次启动程序
            if (!SPUtil.getIsFirst()) {
                //查看shareprefernces中是否保存的UserInfo(登录时保存的)
                UserInfo userInfo = SPUtil.getUserInfo();
                if (userInfo == null) {
                    goLogin();
                } else {
                    goMain(userInfo);
                }
            } else {
                SPUtil.clearSP();
                //软件运行过sp中记录
                SPUtil.setIsFirst(false);
                goIntroduction();
            }
        }
    }

    /**
     * 功能描述:跳转到登录页面
     */
    private void goLogin() {
        mActivity.startActivity(LoginActivity.getIntent(mContext));
        mActivity.finish();
    }

    /**
     * 功能描述: 跳转到主页面
     */
    private void goMain(UserInfo userInfo) {
        UserInfo.setUserInfo((AppApplication) mContext.getApplicationContext(), SPUtil.getUserAccount(), userInfo);
        mActivity.startActivity(MainGridActivity.getIntent(mContext));
        mActivity.finish();
    }

    /**
     * 功能描述: 跳转到引导页面
     */
    private void goIntroduction() {
        mActivity.startActivity(IntroductionActivity.getIntent(mContext));
        mActivity.finish();
    }

}
