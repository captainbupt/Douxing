package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badou.mworking.R;
import com.badou.mworking.util.DensityUtil;
import com.captainhwz.layout.ContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class CategoryTabContent extends LinearLayout implements ContentHandler {

    Context mContext;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;

    List<RadioButton> mRadioButtonList;

    MyViewPagerAdapter mAdapter;

    boolean swipeEnabled;

    public CategoryTabContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_category_content, this, true);
        ButterKnife.bind(this, this);
        mContext = context;
        mAdapter = new MyViewPagerAdapter(((ActionBarActivity) context).getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(3);
        mRadioButtonList = new ArrayList<>();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mViewpager.setCurrentItem(checkedId - 1, true);
            }
        });
    }

    @OnPageChange(R.id.viewpager)
    void onPageChanged(int position) {
        mRadioButtonList.get(position).setChecked(true);
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return ((ScrollableContent) mAdapter.getItem(mViewpager.getCurrentItem())).checkCanDoRefresh(frame, content, header);
    }

    @Override
    public void onChange(final float ratio, final float offsetY) {
        for (int ii = 0; ii < mAdapter.getCount(); ii++) {
            ((ScrollableContent) mAdapter.getItem(ii)).onChange(ratio, offsetY);
        }
    }

    @Override
    public void onOffsetCalculated(final int totalOffset) {
        mViewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int ii = 0; ii < mAdapter.getCount(); ii++) {
                    ((ScrollableContent) mAdapter.getItem(ii)).onOffsetCalculated(totalOffset);
                }
            }
        }, 200);
    }

    public void setSwipeEnabled(boolean isEnable) {
        swipeEnabled = isEnable;
        if (isEnable) {
            for (int ii = 0; ii < mRadioButtonList.size(); ii++) {
                mRadioButtonList.get(ii).setEnabled(true);
            }
        } else {
            for (int ii = 1; ii < mRadioButtonList.size(); ii++) {
                mRadioButtonList.get(ii).setEnabled(false);
            }
        }
        mRadioButtonList.get(0).setChecked(true);
    }

    float lastX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = ev.getX();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE && !swipeEnabled) {
            ev.setLocation(lastX, ev.getY());
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setList(List<ScrollableContent> scrollableList) {
        mAdapter.setList(scrollableList);
        for (int ii = 0; ii < scrollableList.size(); ii++) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            radioButton.setLayoutParams(lp);
            int paddingLess = DensityUtil.getInstance().getOffsetLess();
            radioButton.setPadding(paddingLess, paddingLess, paddingLess, paddingLess);
            radioButton.setBackgroundResource(R.drawable.background_radio_category);
            radioButton.setTextColor(getResources().getColorStateList(R.color.color_radio_button_text_blue_category));
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.getInstance().getTextSizeMedium());
            radioButton.setButtonDrawable(new ColorDrawable(0x00000000));
            radioButton.setId(ii + 1);
            mRadioButtonList.add(radioButton);
            mRadioGroup.addView(radioButton);
            if (mRadioButtonList.size() == 1) {
                radioButton.setChecked(true);
            }
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mViewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int ii = 0; ii < mAdapter.getCount(); ii++) {
                    mRadioButtonList.get(ii).setText(mAdapter.getPageTitle(ii));
                }
            }
        }, 200);
    }

    public interface ScrollableContent {
        boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header);

        String getTitle();

        void onOffsetCalculated(int offset);

        void onChange(float ratio, float offsetY);

    }

    static class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        List<ScrollableContent> mFragmentList;

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<>();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < mFragmentList.size()) {
                return mFragmentList.get(position).getTitle();
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) mFragmentList.get(position);
        }

        public void setList(List<ScrollableContent> list) {
            this.mFragmentList = list;
            notifyDataSetChanged();
        }
    }
}
