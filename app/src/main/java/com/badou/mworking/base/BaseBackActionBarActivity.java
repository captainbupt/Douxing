package com.badou.mworking.base;

import android.os.Bundle;

import com.badou.mworking.R;

/**
 * Created by yee on 3/6/14.
 */
public class BaseBackActionBarActivity extends BaseActionBarActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLeft(R.drawable.title_bar_back);
	}
	
	@Override
	public void clickLeft() {
		super.clickLeft();
		finish();
	}
}
