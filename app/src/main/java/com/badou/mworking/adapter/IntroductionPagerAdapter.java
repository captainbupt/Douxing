package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.badou.mworking.factory.IntroductionViewFactory;

/**
 * Created by Administrator on 2015/5/19.
 * 多个引导页adapter
 */
public class IntroductionPagerAdapter extends PagerAdapter {

    private Context mContext;
    private IntroductionViewFactory mFactory;

    public IntroductionPagerAdapter(Context context, IntroductionViewFactory factory){
        this.mContext = context;
        this.mFactory = factory;
    }

    /**
     * 销毁arg1位置的界面
     */
    @Override
    public void destroyItem(View arg0, int postion, Object arg2) {
        ((ViewPager) arg0).removeView(mFactory.getViewByPosition(postion));
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        return IntroductionViewFactory.COUNT_IMAGE;
    }

    /**
     * 初始化arg1位置的界面
     */
    @Override
    public Object instantiateItem(View arg0, int arg1) {

        ((ViewPager) arg0).addView(mFactory.getViewByPosition(arg1), 0);

        return mFactory.getViewByPosition(arg1);
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }
}