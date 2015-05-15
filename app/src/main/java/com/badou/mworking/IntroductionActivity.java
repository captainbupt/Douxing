package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.SP;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 类:  <code> IntroductionActivity </code>
 * 功能描述: 程序第一次启动的导航页
 * 创建人:董奇
 * 创建日期: 2014年7月15日 下午3:45:45
 * 开发环境: JDK7.0
 */
public class IntroductionActivity extends BaseNoTitleActivity {
	
	private Button button;//开始使用 button
	private ViewPager viewPager;
	private RadioGroup radioGroup;//导航页底部圆点
	private static final int COUNT_IMAGE = 4;//viewpager显示的view数
	public static final String KEY_ISFIRST = AppApplication.appVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//判断是否是第一次启动程序 
		if (!SP.getBooleanSP(mContext,SP.DEFAULTCACHE, KEY_ISFIRST, true)) {
			//查看shareprefernces中是否保存的UserInfo(登录时保存的)
			UserInfo userInfo = UserInfo.getUserInfo(getApplicationContext());
			if (userInfo == null){
				goLogin();
			}else{
				goMain(userInfo);	
			}
			finish();
		} else {
			SP.clearSP(this,SP.DEFAULTCACHE);
			//软件运行过    sp 中记录
			SP.putBooleanSP(mContext,SP.DEFAULTCACHE, KEY_ISFIRST, false);
		} 
		setContentView(R.layout.activity_introductions);
		viewPager = (ViewPager) findViewById(R.id.vp_introduction);
		radioGroup = (RadioGroup) findViewById(R.id.rg_introduction);
		button = (Button) findViewById(R.id.btn_introduction);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				goLogin();
				finish();
			}
		});
		List<ImageView> imageViewList = new ArrayList<ImageView>();
		//获取屏幕尺寸,动态设置viewpager中 imageview的尺寸和显示的内容
		WindowManager manage = getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		for (int i = 0; i < COUNT_IMAGE; i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ScaleType.CENTER_CROP);
			//处理图片防止oom
			imageView.setImageBitmap(BitmapUtil
					.decodeSampledBitmapFromResource(getResources(),
							R.drawable.background_welcome_1 + i, screenWidth,
							screenHeight));
			imageViewList.add(imageView);
		}
		viewPager.setAdapter(new IntroductionAdapter(imageViewList));
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				radioGroup.check(R.id.rb_welcome_1 + position);
				//滑动到最后一个view 显示button
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == COUNT_IMAGE - 1) {
					button.setVisibility(View.VISIBLE);
				} else {
					button.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		viewPager.setCurrentItem(0);
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
		 * 
		 * 功能描述:跳转到登录页面
		 */
		private void goLogin() {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		/**
		 * 
		 * 功能描述: 跳转到主页面
		 * @param userInfo
		 */
		private void goMain(UserInfo userInfo) {
			Intent intent = new Intent(this, MainGridActivity.class);
			//把用户信息保存到Application
			((AppApplication) getApplication()).setUserInfo(userInfo);
			startActivity(intent);
		}
		


	/**
	 * 
	 * 类:  <code> IntroductionAdapter </code>
	 * 功能描述: viewpager的adapter
	 * 创建人:董奇
	 * 创建日期: 2014年7月15日 下午3:49:39
	 * 开发环境: JDK7.0
	 */
	class IntroductionAdapter extends PagerAdapter {

		private List<ImageView> views;

		public IntroductionAdapter(List<ImageView> imageViewList) {
			this.views = imageViewList;
		}

		/**
		 * 销毁arg1位置的界面
		 * */
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		/**
		 * 获得当前界面数
		 * */
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		/**
		 *  初始化arg1位置的界面
		 * */
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		/**
		 * 判断是否由对象生成界面
		 * */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}
	}
}
