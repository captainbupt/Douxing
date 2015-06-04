package com.badou.mworking.base;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.R;
import com.badou.mworking.net.Net;

public class BaseStatisticalActionBarActivity extends BaseBackActionBarActivity{

	public static final String KEY_RID = "rid";

	protected String mRid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLeft(R.drawable.button_title_bar_back_normal);
		// 是否是管理员
		if(((AppApplication) getApplicationContext())
				.getUserInfo().isAdmin){
			setRightImage(R.drawable.button_title_admin_statistical);
		}
	}

	@Override
	public void clickRight() {
		String titleStr = getResources().getString(R.string.statistical_data);
		String uid = ((AppApplication) getApplicationContext()).getUserInfo().userId;
		String url = Net.getRunHost(mContext) + Net.getTongji(uid, mRid);
		Intent intent = new Intent(mContext, BackWebActivity.class);
		intent.putExtra(BackWebActivity.KEY_URL, url);
		intent.putExtra(BackWebActivity.KEY_TITLE, titleStr);
		startActivity(intent);
	}
}
