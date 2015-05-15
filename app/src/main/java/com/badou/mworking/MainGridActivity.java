package com.badou.mworking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.BannerAdapter;
import com.badou.mworking.adapter.MainGridAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.MainBanner;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.widget.BannerGallery;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 类: <code> MainGridActivity </code> 功能描述: 主页面 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午3:37:14 开发环境: JDK7.0
 */
public class MainGridActivity extends BaseNoTitleActivity implements View.OnClickListener{

	private long exitTime = 0; // 记录系统时间
	private MainGridAdapter mainGridAdapter;
	public static final String KEY_TITLE_NAME = "KEY_TITLE_NAME";

	public static String finalImgPath = "";
	private GridView gridView;

	private ImageView iconTop; // 企业log imageview
	private int access = 255;
	/**保存banner的list**/
	private ArrayList<MainBanner> bannerList;
	/**显示bannner**/
	private BannerGallery bannerGallery = null;
	/**小原点**/
	private ArrayList<ImageView> portImg;
	private int preSelImgIndex = 0; // 存储上一个选择项的Index
	private LinearLayout ll_focus_indicator_container = null;
	
	private ImageView rightImg;    //logo 布局左边图表，点击进入个人中心
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_grid);
		JPushInterface.init(getApplicationContext());
		initView();
		int isNewUser = 1;
		isNewUser = SP.getIntSP(mContext,SP.DEFAULTCACHE, ResponseParams.EXPER_IS_NEW_USER, 0);
		if (1 == isNewUser) {
			new AlertDialog.Builder(MainGridActivity.this).setTitle("欢迎回来!").setMessage(getResources().getString(R.string.main_tips_olduser)).setPositiveButton("确定", null).show();
			SP.putIntSP(mContext,SP.DEFAULTCACHE, ResponseParams.EXPER_IS_NEW_USER, 0);
		}
		try {
			access = ((AppApplication) this.getApplicationContext())
					.getUserInfo().getAccess();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		// 取出SP中的banner内容
		bannerList = new ArrayList<MainBanner>();
		String bannerStr = SP.getStringSP(this, SP.DEFAULTCACHE,"banner", "");
		if (bannerStr != null && !bannerStr.equals("")) {
			String[] bannerInfos = bannerStr.split(",");
			for (String string : bannerInfos) {
				String[] bannerInfo = string.split("@");
				MainBanner mainBanner = new MainBanner(bannerInfo[0],
						bannerInfo[1], bannerInfo[2]);
				bannerList.add(mainBanner);
			}
		}
		updateBanner(bannerList);

		
		setMainIconData();   
		// 获取mainIcon信息
		MainIcon mainIcon = new MainIcon();
		List<MainIcon> mainIcons = mainIcon.getMainIcons(this);

		mainGridAdapter = new MainGridAdapter(mContext, access, mainIcons);
		gridView.setAdapter(mainGridAdapter);
		initListener();

		// 调用缓存中的企业logoUrl图片，这样断网的情况也会显示出来了，如果本地没有的话，网络获取
		String logoUrl = SP.getStringSP(this,SP.DEFAULTCACHE, "logoUrl", "");
		initCompanyLog(logoUrl);
		checkUpdate();
		
		AlarmUtil alarmUtil = new AlarmUtil();
		alarmUtil.OpenTimer(this);
		
		//push 推送默认开启，如果用户关闭掉推送的话，在这里停掉推送
		Boolean isOpenPush = SP.getBooleanSP(this,SP.DEFAULTCACHE, Constant.PUSH_NOTIFICATIONS, true); 
		if(!isOpenPush){
			JPushInterface.stopPush(getApplicationContext());
		}
	}

	/**
	 * 功能描述: 更新显示的banner
	 * @param bList
	 */
	private void updateBanner(final ArrayList<MainBanner> bList) {
		InitFocusIndicatorContainer(bList);
		bannerGallery.setAdapter(new BannerAdapter(MainGridActivity.this,
				bList));
		bannerGallery.setFocusable(true);
		if (bList.size() <= 0) {
			return;
		}
		bannerGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int selIndex, long arg3) {
				
				selIndex = selIndex % bList.size();
				// 修改上一次选中项的背景
				portImg.get(preSelImgIndex).setImageResource(
						R.drawable.background_rb_welcome_unselected);
				// 修改当前选中项的背景
				portImg.get(selIndex).setImageResource(
						R.drawable.background_rb_welcome_selected);
				preSelImgIndex = selIndex;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		/**
		 * 点击跳转到webview页
		 */
		bannerGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position % bList.size();
				Intent intent = new Intent(mContext, BackWebActivity.class);
				intent.putExtra(BackWebActivity.VALUE_URL, bList.get(pos).getBannerContentURL()+"");
				BackWebActivity.PAGEFLAG = BackWebActivity.BANNER;
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(mainGridAdapter!=null){
			mainGridAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 功能描述: 点击两次返回键退出应用程序，通过记录按键时间计算时间差实现
	 */
	public void exit() {
		// 应为系统当前的系统毫秒数一定小于2000
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), R.string.main_exit_tips,
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			AppManager.getAppManager().AppExit(this, false);
		}
	}

	/**
	 * 功能描述: 通知公告
	 */
	private void go1(String titleName) {
		Category.CLICKMAINICON = Category.CATEGORY_NOTICE;
		Intent intent = new Intent(mContext, NoticesActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述:微培训
	 */
	private void go2(String titleName) {
		Category.CLICKMAINICON = Category.CATEGORY_TRAIN;
		Intent intent = new Intent(mContext, TrainActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述:在线考试
	 */
	private void go3(String titleName) {
		Category.CLICKMAINICON = Category.CATEGORY_EXAM;
		Intent intent = new Intent(mContext, ExamActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述: 培训调研
	 */
	private void go4(String titleName) {
		String uid = ((AppApplication) this.getApplicationContext())
				.getUserInfo().getUserId();
		String url = Net.getWeiDiaoYanURl()+uid;
		Intent intent = new Intent(mContext, BackWebActivity.class);
		intent.putExtra(BackWebActivity.VALUE_TITLE, titleName + "");
		intent.putExtra(BackWebActivity.VALUE_URL, url);
		startActivity(intent);
	}

	/**
	 * 功能描述: 任务签到
	 */
	private void go5(String titleName) {
		Category.CLICKMAINICON = Category.CATEGORY_TASK;
		Intent intent = new Intent(mContext, TaskActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述: 同事圈
	 */
	private void go6(String titleName) {
		Intent intent = new Intent(mContext, AroundActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述: 问答模块
	 */
	private void go8(String titleName) {
		Intent intent = new Intent(mContext, WenDActivity.class);
		intent.putExtra(KEY_TITLE_NAME, titleName + "");
		startActivity(intent);
	}

	/**
	 * 功能描述:初始化view
	 */
	private void initView() {
		rightImg = (ImageView) findViewById(R.id.right_img);
		rightImg.setVisibility(View.VISIBLE);
		rightImg.setOnClickListener(this);
		iconTop = (ImageView) findViewById(R.id.icon_top);
		gridView = (GridView) findViewById(R.id.gv_main_grid_second);
		ll_focus_indicator_container = (LinearLayout) findViewById(R.id.ll_focus_indicator_container);
		bannerGallery = (BannerGallery) findViewById(R.id.gallery);
	}

	/**
	 * 功能描述:设置view的监听
	 */
	private void initListener() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ImageView imageView = (ImageView) arg1
						.findViewById(R.id.iv_adapter_main_grid);
				TextView tvName = (TextView) arg1
						.findViewById(R.id.tv_adapter_main_name);
				String tag = (String) imageView.getTag();
				String name = "";
				switch (tag) {
				case RequestParams.CHK_UPDATA_PIC_NOTICE: // 通知公告
					name = tvName.getText().toString();
					go1(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_TRAIN: // 微培训
					name = tvName.getText().toString();
					go2(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_EXAM: // 在线考试
					name = tvName.getText().toString();
					go3(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_SURVEY: // 培训调研
					name = tvName.getText().toString();
					go4(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_TASK: // 任务签到
					name = tvName.getText().toString();
					go5(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_CHATTER: // 同事圈
					name = tvName.getText().toString();
					go6(name);
					break;
				case RequestParams.CHK_UPDATA_PIC_ASK: //问答
					name = tvName.getText().toString();
					go8(name);
					break;
				}
				overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			}
		});
	}

	/**
	 * 功能描述:网络请求更新资源包，         这里是上传MD5来进行匹配，应为本地icon图片已经缓存，每次上传null，会把完整信息请求下来，
	 * 如果url匹配，不会再下载图片内容
	 */
	private void checkUpdate() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(RequestParams.CHK_UPDATA_PIC_COMPANY_LOGO, "");
			jsonObject.put(RequestParams.CHK_UPDATA_BANNER, "");
			jsonObject.put(RequestParams.CHK_UPDATA_PIC_NEWVER, "");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		/**
		 * 发起请求
		 */
		ServiceProvider.doCheckUpdate(mContext, jsonObject, new VolleyListener(
				mContext) {

			@Override
			public void onResponse(Object responseObject) {
				ArrayList<MainBanner> list = new ArrayList<MainBanner>();
				JSONObject response = (JSONObject) responseObject;
				try {
					int code = response.optInt(Net.CODE);
					if (code == Net.LOGOUT) {
						AppApplication.logoutShow(mContext);
						return;
					}
					if (code != Net.SUCCESS) {
						return;
					}
					JSONObject data = response.optJSONObject(Net.DATA);
					apkUpdate(data);

					JSONObject jSONObject = data
							.optJSONObject(RequestParams.CHK_UPDATA_PIC_COMPANY_LOGO);
					if (jSONObject != null) {
						String logoUrl = jSONObject
								.optString(MainBanner.CHK_URL);
						SP.putSP(MainGridActivity.this,SP.DEFAULTCACHE, "logoUrl", logoUrl);
						initCompanyLog(logoUrl);
					}

					JSONArray arrBanner = data.getJSONArray("banner");

					String bannerInfo = "";

					for (int i = 0; i < arrBanner.length(); i++) {
						JSONObject jo = (JSONObject) arrBanner.get(i);
						String img = jo.optString(MTrainingDBHelper.CHK_IMG);
						String url = jo.optString(MainBanner.CHK_URL);
						String md5 = jo.optString(MainBanner.CHK_RES_MD5);
						MainBanner banner = new MainBanner(img, url, md5);
						list.add(banner);
						bannerInfo = bannerInfo
								+ banner.bannerToString(img, url, md5);
					}
					updateBanner(list);
					// 保存banner信息数据到sp
					SP.putStringSP(MainGridActivity.this,SP.DEFAULTCACHE, "banner", bannerInfo);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}

	/**
	 * 在主页验证是否有软件更新
	 * 
	 * @param jsons
	 *            data的value
	 */
	private void apkUpdate(JSONObject dataJson) {
		JSONObject newVerjson = dataJson
				.optJSONObject(RequestParams.CHK_UPDATA_PIC_NEWVER);
		boolean hasNew = newVerjson.optInt(ResponseParams.CHECKUPDATE_NEW) == 1;
		if (hasNew) {
			final String info = newVerjson
					.optString(ResponseParams.CHECKUPDATE_INFO);
			final String url = newVerjson
					.optString(ResponseParams.CHECKUPDATE_URL);
			new AlertDialog.Builder(mContext)
					.setTitle(R.string.main_tips_update_title)
					.setMessage(info)
					.setPositiveButton(R.string.main_tips_update_btn_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ServiceProvider.doUpdateMTraning(mActivity,
											url);
								}
							}).setNegativeButton(R.string.text_cancel, null)
					.create().show();
		}
	}

	/**
	 * 
	 * 功能描述:初始化MainIcon的数据
	 * @param json
	 * @param key
	 * @return
	 * @throws org.json.JSONException
	 */
	private void setMainIconData() {               
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_NOTICE,R.drawable.button_notice+"","通知公告");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_CHATTER,R.drawable.button_chatter+"","同事圈");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_EXAM,R.drawable.button_exam+"","在线考试");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_SURVEY,R.drawable.button_survey+"","培训调研");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_TASK,R.drawable.button_task+"","任务签到");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_TRAIN,R.drawable.button_training+"","微培训");
		setMainIconDataToSP(RequestParams.CHK_UPDATA_PIC_ASK,R.drawable.button_ask+"","问答");
	}
	
	/**
	 * @param key icon键值
	 * @param nativeUrl   本地图片
	 * @param defaultTitle 默认名称
	 */
	private void setMainIconDataToSP(String key,String nativeUrl,String defaultTitle){
		JSONObject name_priority = updateMainIconPriority(key);
		String title = name_priority.optString("name");
		String mainIconId = key;
		String priority = name_priority.optString("priority");
		String url = nativeUrl;
		if(TextUtils.isEmpty(title)){
			title = defaultTitle;
		}
		MainIcon mi = new MainIcon();
		String mainIconInfoStr = mi.mainIconToString(mainIconId,url,title,priority);
		SP.putStringSP(MainGridActivity.this,SP.DEFAULTCACHE, mainIconId, mainIconInfoStr);
	}
	
	/**
	 * 功能描述: 更新数据库中mainIcon的name 字段和 priority 字段
	 */
	private JSONObject updateMainIconPriority(String key) {
		String shuffleStr = SP.getStringSP(MainGridActivity.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, "");
		if(TextUtils.isEmpty(shuffleStr)){
			return null;
		}
		try {
			JSONObject shuffle = new JSONObject(shuffleStr);
			Iterator it = shuffle.keys();
			while (it.hasNext()) {
				String IconKey = (String) it.next();
				if (key.equals(IconKey)) {
					return shuffle.optJSONObject(IconKey);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能描述: 初始化企业logo布局
	 */
	private void initCompanyLog(String logoUrl) {
		Bitmap logBmp = BitmapLruCache.getBitmapLruCache().get(logoUrl);
		if (logBmp != null && logBmp.isRecycled()) {
			iconTop.setImageBitmap(logBmp);
		} else {
			MyVolley.getImageLoader().get(
					logoUrl,
					new IconLoadListener(mContext, iconTop, logoUrl,
							R.drawable.logo));
		}
	}

	/**
	 * 功能描述: 定义底部滑动的小点
	 */
	private void InitFocusIndicatorContainer(ArrayList<MainBanner> blist) {
		this.ll_focus_indicator_container.removeAllViews();
		if (blist == null || blist.size()<=0) {
			return;
		}
		portImg = new ArrayList<ImageView>();
		for (int i = 0; i < blist.size(); i++) {
			ImageView localImageView = new ImageView(MainGridActivity.this);
			localImageView.setId(i);
			localImageView.setScaleType(ScaleType.FIT_XY);
			LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
					24, 24);
			localImageView.setLayoutParams(localLayoutParams);
			localImageView.setPadding(5, 5, 5, 5);
			localImageView
					.setImageResource(R.drawable.background_rb_welcome_unselected);
			portImg.add(localImageView);
			this.ll_focus_indicator_container.addView(localImageView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_img:
			Intent intent = new Intent(mContext, UserCenterActivity.class);
			intent.putExtra(KEY_TITLE_NAME, "个人中心");
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;

		default:
			break;
		}
	}


}
