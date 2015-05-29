package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.fragment.TongSHQFragments;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 类: <code> AroundActivity </code> 功能描述: 同事圈页面 创建人: 葛建锋 创建日期: 2014年8月21日
 * 下午3:56:38 开发环境: JDK7.0
 */
public class AroundActivity extends BaseNoTitleActivity implements OnClickListener {

	
	public ImageView ivLeft;	// action 左侧iv 
	public ImageView ivRight;	//action 右侧 iv 
	public TextView tvTitle; 	// action 中间tv
	private TextView commentRelat; //底部我要分享按钮
	
	public ProgressBar updatePro; // 刷新进度条
	private SwipeBackLayout layout;

	public static final String BUNDLE_MODE_KEY = "BUNDLE_MODE_KEY";
	public static final String KEY_TITLE_NAME = "AroundActivityTITLE_NAME";
	public static boolean ANIM_IS_FINISH = false;
	private Context mContext;

	TongSHQFragments af;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_tong_shi_quan);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		mContext = AroundActivity.this;
		initAction(this);
		tvTitle.setText(getIntent().getStringExtra(BaseActionBarActivity.KEY_TITLE)+"");
		af = new TongSHQFragments();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_around, af).commit();
		
		if(NetUtils.isNetConnected(this)){
			boolean isWifi = NetUtils.isWifiConnected(mContext);
			boolean isShowImg = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,"pic_show", false);
			if(!isWifi&&isShowImg){
				ToastUtil.showToast(this, getResources().getString(R.string.tips_liuliang));
			}
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

	/**
	 * c初始化action 布局
	 * @param onclick
	 */
	private void initAction(OnClickListener onclick) {
		updatePro = (ProgressBar) findViewById(R.id.pb_action_bar);
		ivLeft = (ImageView)findViewById(R.id.iv_actionbar_left);
		tvTitle = (TextView)findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView)findViewById(R.id.iv_actionbar_right);
		commentRelat = (TextView) findViewById(R.id.tv_user_progress_bottom);
		ivLeft.setOnClickListener(onclick);
		ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		tvTitle.setText(getResources().getString(R.string.user_center_my_exam));
		commentRelat.setText(getResources().getString(R.string.tongshiquan_share));
		commentRelat.setOnClickListener(this);
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 
		 //触发fm的
		af.onActivityResult(requestCode, resultCode, data); 
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_actionbar_left:
			finish();
			break;
		case R.id.tv_user_progress_bottom:
			Intent intent = new Intent(mContext, QuestionActivity.class);
			intent.putExtra(KEY_TITLE_NAME, tvTitle.getText().toString()+"");
			mContext.startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

}
