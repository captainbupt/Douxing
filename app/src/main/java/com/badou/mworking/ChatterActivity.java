package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.ChatterHotFragment;
import com.badou.mworking.fragment.ChatterListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 同事圈页面
 */
public class ChatterActivity extends BaseBackActionBarActivity {

    private RadioGroup mRadioGroup;
    private RadioButton mChatterRadioButton;
    private RadioButton mHotRadioButton;
    private ViewPager mContentViewPager;
    private FragmentPagerAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter);
        initView();
        initListener();
        initData();
    }


    /**
     * c初始化action 布局
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRadioGroup = (RadioGroup) inflater.inflate(R.layout.actionbar_activity_chatter, null);
        setTitleCustomView(mRadioGroup);
        mChatterRadioButton = (RadioButton) mRadioGroup.findViewById(R.id.rb_activity_chatter_title_left);
        mHotRadioButton = (RadioButton) mRadioGroup.findViewById(R.id.rb_activity_chatter_title_right);
        mContentViewPager = (ViewPager) findViewById(R.id.vp_activity_chatter);
        mFragmentAdapter = new ChatterFragmentPagerAdapter(getSupportFragmentManager());
        mContentViewPager.setAdapter(mFragmentAdapter);
    }

    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.rb_activity_chatter_title_left) {
                    mContentViewPager.setCurrentItem(0);
                } else if (id == R.id.rb_activity_chatter_title_right) {
                    mContentViewPager.setCurrentItem(1);
                }
            }
        });
        mContentViewPager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0)
                    mChatterRadioButton.setChecked(true);
                else
                    mHotRadioButton.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void initData() {
        mChatterRadioButton.setText(mReceivedIntent.getStringExtra(BaseActionBarActivity.KEY_TITLE));
        mChatterRadioButton.setChecked(true);
        setRightText(R.string.chatter_title_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void clickRight() {
        Intent intent = new Intent(mContext, ChatterSubmitActivity.class);
        startActivity(intent);
    }


    static class ChatterFragmentPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> mFragments;

        public ChatterFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
            mFragments.add(new ChatterListFragment());
            mFragments.add(new ChatterHotFragment());
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
