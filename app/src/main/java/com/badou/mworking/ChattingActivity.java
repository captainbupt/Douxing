package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.fragment.ChattingFragment;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;

/**
 * 类:  <code> ChattingActivity </code>
 * 功能描述: 聊天页面
 * 创建人:  葛建锋
 * 创建日期: 2014年9月18日 下午7:05:20
 * 开发环境: JDK7.0
 */
public class ChattingActivity extends BaseNoTitleActivity implements OnClickListener{
	
	public static final String CHAT_TAG = "ChattingActivity_";
	ChattingFragment fragment;
	
	private SwipeBackLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragact_chat);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		initAction(this);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		fragment = new ChattingFragment();
		ft.add(R.id.layout_fragment, fragment).commit();
	}
	
	/**
	 * c初始化action 布局
	 * @param onclick
	 */
	private void initAction(OnClickListener onclick) {
		ImageView ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		TextView tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		ImageView ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		ivLeft.setOnClickListener(onclick);
		ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		// 以前该模块名称为聊天，在首页显示，通过access字段控制，所以sp中保存缓存信息，名称是后台可配置的，现在放到个人中心中，直接命名为直通车
		/***从sp中查询title显示的text**/
//		String titleName= SP.getStringSP(mContext, RequestParams.CHK_UPDATA_PIC_CHAT, "");
//		MainIcon mainIcon = new MainIcon();
//		tvTitle.setText(mainIcon.getMainIcon(titleName).getName());
		tvTitle.setText(getResources().getString(R.string.my_sixing));
		ivRight.setOnClickListener(onclick);
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setImageResource(R.drawable.search);
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
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_actionbar_left:
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.iv_actionbar_right:
			Intent intent  = new Intent(mContext, TitleSearchAct.class);
			intent.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, TitleSearchAct.SEARCH_CHAT);
			intent.putExtra(CHAT_TAG,(Serializable)fragment.getListData());
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
