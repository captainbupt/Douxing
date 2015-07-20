package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.badou.mworking.adapter.IntroductionPagerAdapter;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.presenter.IntroductionPresenter;
import com.badou.mworking.view.IntroductionView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * 第一次启动程序的引导页面
 */
public class IntroductionActivity extends BaseNoTitleActivity implements IntroductionView {

    @Bind(R.id.introduction_view_pager)
    ViewPager mIntroductionViewPager;
    @Bind(R.id.introduction_button)
    TextView mIntroductionTextView;
    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;
    @Bind({R.id.radio_button_1, R.id.radio_button_2, R.id.radio_button_3, R.id.radio_button_4})
    List<RadioButton> mRadioButtonList;

    IntroductionPresenter introductionPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, IntroductionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductions);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        disableSwipeBack();
        mIntroductionViewPager.setAdapter(new IntroductionPagerAdapter(mContext));
        mIntroductionViewPager.setCurrentItem(0);
        introductionPresenter = new IntroductionPresenter(mContext);
        introductionPresenter.attachView(this);
        mRadioButtonList.get(0).setChecked(true);
    }

    @OnPageChange(value = R.id.introduction_view_pager, callback = OnPageChange.Callback.PAGE_SCROLLED)
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        introductionPresenter.onPageScrolled(position, positionOffset, positionOffsetPixels);
        mRadioButtonList.get(position).setChecked(true);
    }

    @OnClick(R.id.introduction_button)
    void onIntroductionClick() {
        introductionPresenter.onIntroductionClick();
    }

    @Override
    public void showIntroduction() {
        mRadioGroup.setVisibility(View.INVISIBLE);
        mIntroductionTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideIntroduction() {
        mIntroductionTextView.setVisibility(View.INVISIBLE);
        mRadioGroup.setVisibility(View.VISIBLE);
    }
}
