package com.badou.mworking.base;

import android.os.Bundle;

import com.badou.mworking.R;

public class BaseBackActionBarActivity extends BaseActionBarActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLeft(R.drawable.title_bar_back_normal);
	}

	@Override
	public void clickLeft() {
		super.clickLeft();
		finish();
	}
}
