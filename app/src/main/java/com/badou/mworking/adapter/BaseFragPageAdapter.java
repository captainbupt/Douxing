package com.badou.mworking.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 类:  <code> BaseFragPageAdapter </code>
 * 功能描述:  模块列表页的Fragment 适配器
 * 创建人:  葛建锋
 * 创建日期: 2014年8月19日 上午9:43:08
 * 开发环境: JDK7.0
 */
public class BaseFragPageAdapter extends FragmentPagerAdapter{

	
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public BaseFragPageAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public BaseFragPageAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}

}
