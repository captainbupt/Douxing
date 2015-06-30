package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.badou.mworking.adapter.IntroductionPagerAdapter;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.presenter.IntroductionPresenter;
import com.badou.mworking.view.IntroductionView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * 第一次启动程序的引导页面
 */
public class IntroductionActivity extends BaseNoTitleActivity implements IntroductionView {

    @InjectView(R.id.introduction_view_pager)
    ViewPager mIntroductionViewPager;
    @InjectView(R.id.introduction_button)
    Button mIntroductionButton;

    IntroductionPresenter introductionPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, IntroductionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductions);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        disableSwipeBack();
        mIntroductionViewPager.setAdapter(new IntroductionPagerAdapter(mContext));
        mIntroductionViewPager.setCurrentItem(0);
        introductionPresenter = new IntroductionPresenter(mContext);
        introductionPresenter.attachView(this);
    }


    @OnPageChange(value = R.id.introduction_view_pager, callback = OnPageChange.Callback.PAGE_SCROLLED)
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        introductionPresenter.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @OnClick(R.id.introduction_button)
    void onIntroductionClick() {
        introductionPresenter.onIntroductionClick();
    }

    @Override
    public void showIntroduction() {
        mIntroductionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideIntroduction() {
        mIntroductionButton.setVisibility(View.GONE);
    }
}
