package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badou.mworking.R;

/**
 * Created by Administrator on 2015/5/19.
 * 多个引导页adapter
 */
public class IntroductionPagerAdapter extends PagerAdapter {

    public static final int COUNT_IMAGE = 4;

    private View[] mViewArray;

    public IntroductionPagerAdapter(Context context) {
        this.mViewArray = createViews(context);
    }

    private View[] createViews(Context context) {
        View[] views = new View[COUNT_IMAGE];

        for (int i = 0; i < COUNT_IMAGE; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.background_welcome_1 + i);
            views[i] = imageView;
        }
        return views;
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