package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Administrator on 2015/5/19.
 * 多个引导页adapter
 */
public class IntroductionPagerAdapter extends PagerAdapter {

    private Context mContext;

    private View[] mViewArray;

    public IntroductionPagerAdapter(Context context, View[] viewArray) {
        this.mContext = context;
        this.mViewArray = viewArray;
    }

    /**
     * 销毁arg1位置的界面
     */
    @Override
    public void destroyItem(View arg0, int postion, Object arg2) {
        ((ViewPager) arg0).removeView(mViewArray[postion]);
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        return mViewArray.length;
    }

    /**
     * 初始化arg1位置的界面
     */
    @Override
    public Object instantiateItem(View arg0, int arg1) {

        ((ViewPager) arg0).addView(mViewArray[arg1], 0);

        return mViewArray[arg1];
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }
}