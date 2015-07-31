package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.fragment.ChatterHotFragment;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.chatter.ChatterPresenter;
import com.badou.mworking.view.chatter.ChatterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

/**
 * 功能描述: 同事圈页面
 */
public class ChatterActivity extends BaseBackActionBarActivity implements ChatterView {

    @Bind(R.id.content_view_pager)
    ViewPager mContentViewPager;

    private RadioGroup mRadioGroup;
    private RadioButton mChatterRadioButton;
    private RadioButton mHotRadioButton;
    private FragmentPagerAdapter mFragmentAdapter;

    ChatterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter);
        ButterKnife.bind(this);
        initView();
        initListener();
        mPresenter = (ChatterPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new ChatterPresenter(mContext);
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
        mFragmentAdapter = new ChatterFragmentPagerAdapter(getSupportFragmentManager());
        mContentViewPager.setAdapter(mFragmentAdapter);
        mChatterRadioButton.setText(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, Shuffle.BUTTON_CHATTER).getName());
    }

    private void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.rb_activity_chatter_title_left) {
                    mPresenter.onPageSelected(0);
                } else if (id == R.id.rb_activity_chatter_title_right) {
                    mPresenter.onPageSelected(1);
                }
            }
        });
        setRightText(R.string.chatter_title_right, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.publishChatter();
            }
        });
    }

    @OnPageChange(R.id.content_view_pager)
    void onPageSelected(int position) {
        mPresenter.onPageSelected(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setChatterPage() {
        mChatterRadioButton.setChecked(true);
        mContentViewPager.setCurrentItem(0);
    }

    @Override
    public void setHotPage() {
        mHotRadioButton.setChecked(true);
        mContentViewPager.setCurrentItem(1);
    }

    @Override
    public void refresh() {
        ((ChatterListFragment) mFragmentAdapter.getItem(0)).startRefreshing();
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
