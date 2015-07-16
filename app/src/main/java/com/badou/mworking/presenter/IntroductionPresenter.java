package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.adapter.IntroductionPagerAdapter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.IntroductionView;

public class IntroductionPresenter extends Presenter {

    IntroductionView introductionView;

    public IntroductionPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        this.introductionView = (IntroductionView) v;
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == IntroductionPagerAdapter.COUNT_IMAGE - 1) {
            introductionView.showIntroduction();
        } else {
            introductionView.hideIntroduction();
        }
    }

    public void onIntroductionClick() {
        // 引导页面之后必然是登陆页面
        mContext.startActivity(LoginActivity.getIntent(mContext));
        ((Activity)mContext).finish();
    }
}
