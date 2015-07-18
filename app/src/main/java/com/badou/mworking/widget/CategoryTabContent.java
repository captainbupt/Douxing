package com.badou.mworking.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.captainhwz.layout.ContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class CategoryTabContent extends LinearLayout implements ContentHandler {

    Context mContext;
    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;

    List<RadioButton> radioButtonList;
    MyViewPagerAdapter adapter;

    public CategoryTabContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_category_content, this, true);
        mContext = context;
        adapter = new MyViewPagerAdapter(((ActionBarActivity) context).getSupportFragmentManager());
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return ((Scrollable) adapter.getItem(mViewpager.getCurrentItem())).checkCanDoRefresh(frame, content, header);
    }

    public void addItem(String title, Scrollable scrollable){
        RadioButton radioButton = new RadioButton(mContext);
        radioButton.setText(title);
        radioButton.setGravity(Gravity.CENTER);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        radioButton.setLayoutParams(lp);
    }

    public interface Scrollable {
        boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header);
    }

    static class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Scrollable> mFragmentList;

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) mFragmentList.get(position);
        }

        public void addItem(Scrollable scrollable){
            mFragmentList.add(scrollable);
        }
    }
}
