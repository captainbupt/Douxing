package com.badou.mworking;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.MyGroupFragment;
import com.badou.mworking.receiver.JPushReceiver;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 我的圈页面
 */
public class MyGroupActivity extends BaseBackActionBarActivity implements
                 MyGroupFragment.OnFragmentInteractionListener {

	public static final String KEY_QID = "qid";

	public static String qid = ""; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arounduseractivity);
		setActionbarTitle(getResources().getString(
				R.string.user_center_my_group));
		setLeft(R.drawable.title_bar_back);
		layout.attachToActivity(this);
		MyGroupFragment af = new MyGroupFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.fragment_around,af).commit();
		
		try {
			String JPushBundle = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
			JSONObject extraJson = new JSONObject(JPushBundle);
			qid = extraJson.getString(JPushReceiver.TYPE_ADD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void clickLeft() {
		this.finish();
	}
	
	@Override
	public void onFragmentInteraction(String id) {

	}

}
