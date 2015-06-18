package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.badou.mworking.adapter.IntroductionPagerAdapter;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.widget.OptimizedImageView;

/**
 * 第一次启动程序的引导页面
 */
public class IntroductionActivity extends BaseNoTitleActivity {

    private Button button;//开始使用 button
    private ViewPager viewPager;
    public static final int COUNT_IMAGE = 4;//viewpager显示的view数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductions);
        initView();
        initListener();
        disableSwipeBack();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_introduction);
        viewPager.setAdapter(new IntroductionPagerAdapter(mContext, createViews(mContext)));
        viewPager.setCurrentItem(0);
        button = (Button) findViewById(R.id.btn_introduction);
    }

    private void initListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (arg0 == COUNT_IMAGE - 1) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 引导页面之后必然是登陆页面
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private View[] createViews(Context context) {
        View[] views = new View[COUNT_IMAGE];

        for (int i = 0; i < COUNT_IMAGE; i++) {
            OptimizedImageView imageView = new OptimizedImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResourceFullScreen(R.drawable.background_welcome_1 + i);
            views[i] = imageView;
        }
        return views;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
