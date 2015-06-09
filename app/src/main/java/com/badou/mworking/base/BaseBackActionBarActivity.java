package com.badou.mworking.base;

import android.os.Bundle;

import com.badou.mworking.R;

public class BaseBackActionBarActivity extends BaseActionBarActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLeft(R.drawable.button_title_bar_back_black);
	}

	@Override
	public void clickLeft() {
		super.clickLeft();
		finish();
	}
}
