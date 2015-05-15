package com.badou.mworking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.receiver.JPushReceiver;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * @author gejianfeng
 * ExamActivity 考试页面
 */ 
public class ExamActivity extends BaseFragmentActivity implements OnClickListener,OnRefreshListener2<ListView>{
	
	private SearchMainAdapter oneadapter1 = null;
	private SearchMoreAdapter twoadapter1 = null;
	
	private ArrayList<Classification> classifications = new ArrayList<Classification>();
	private ArrayList<Classification> classificationsTemp = new ArrayList<Classification>();
	
	private ImageView ivLeft;  //action 左侧iv
	private ImageView triangleDownImg;   //下拉的图标
	private ImageView ivRight;  	//action 右侧 iv
	private ImageView tvSearchNull;
	private TextView tvTitle;  //action 中间tv
	private ListView mShoplist_onelist1;
	private ListView mShoplist_twolist1;
	private LinearLayout titleLay;  // title 的布局
	private LinearLayout classificationLinear;  // 下拉布局
	private PullToRefreshListView pullToRefreshListView; // 下拉刷新listview
	
	public static int tag = 0;
	private int beginIndex = 0;
	private String userNum = "";
	
	public static final int REFRESH_EXAM_LV = 0x00002;
	private ExamAdapter examAdapter;
	public static ArrayList<Exam> list;        // 获取到的list集合
	public static String CLASSIFICATIONNAME = "";    // 试题分类名称

	private View lastView;// listview 的footer 主要在我的学习进度页面用到占位

	public static boolean isRefresh = false;

	public static String examRid = "";    //考试资源id
	private int mainListClickPosition = 0;
	
	private ProgressBar updatePro; // 刷新进度条
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_name_notice);
		layout.attachToActivity(this);
		ExamActivity.tag = 0;
		initView();
		ivRight.setVisibility(View.VISIBLE);
		triangleDownImg.setVisibility(View.VISIBLE);
		String titleName = SP.getStringSP(ExamActivity.this,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_EXAM, "");
		MainIcon mainIcon = new MainIcon();
		tvTitle.setText(mainIcon.getMainIcon(titleName).getName());
		if (getIntent().getIntExtra(MyExamAct.VALUE_EXAM,0) == 1) {
			ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		}else {
			ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		}
		try {
			ExamActivity.examRid = "";       //先清空ExamActivity.examRid对象ExamActivity
			// 如果8点提醒点击进入的话，这里会报空，应为极光推送没有收到内容，在这里做个异常捕获
			String JPushBundle = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
			if(JPushBundle!=null){
				JSONObject extraJson = new JSONObject(JPushBundle);
				ExamActivity.examRid = extraJson.getString(JPushReceiver.TYPE_ADD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Onelistclick1 onelistclick1 = new Onelistclick1();
		Twolistclick twolistclick = new Twolistclick();
		mShoplist_onelist1.setOnItemClickListener(onelistclick1);
		mShoplist_twolist1.setOnItemClickListener(twolistclick);
		titleLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(View.GONE == classificationLinear.getVisibility()){
					classificationLinear.setVisibility(View.VISIBLE);
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_up);
					if(classifications!=null&&classifications.size()>0){
						int main = SP.getIntSP(ExamActivity.this, SP.EXAM, "main", 0);
						int more = SP.getIntSP(ExamActivity.this, SP.EXAM, "more", 0);
						oneadapter1.setSelectItem(main);
						oneadapter1.notifyDataSetChanged();
						classificationsTemp = classifications.get(main).getClassifications();
						if(twoadapter1!=null){
							if(classificationsTemp == null ||classificationsTemp.size()==0){
								initAdapter1(classificationsTemp);
							}else{
								initAdapter1(classificationsTemp);
								twoadapter1.setSelectItem(more);
							}
							twoadapter1.notifyDataSetChanged();
						}
					}
					Animation anim = AnimationUtils.loadAnimation(ExamActivity.this, R.anim.popup_enter);
					classificationLinear.startAnimation(anim);
				}else{
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_down);
					classificationLinear.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(ExamActivity.this, R.anim.popup_exit);
					classificationLinear.startAnimation(anim);
				}
			}
		});
		initListener();
		getClassifications();
		if(ToastUtil.showNetExc(ExamActivity.this)){
			String classificationStr =  SP.getStringSP(ExamActivity.this, SP.EXAM,Exam.CATEGORY_EXAM, "");
			try {
				JSONArray jsonArray = new JSONArray(classificationStr);
				setClassifications(jsonArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		getCash(ExamActivity.tag);
	}
	
	/**
	 * 初始化view
	 * @param view
	 */
	private void initView() {
		updatePro = (ProgressBar) findViewById(R.id.update_pro);
		tvSearchNull = (ImageView)findViewById(R.id.tv_tishi);
		ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		ivLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		triangleDownImg = (ImageView) findViewById(R.id.triangle_down_img);
		titleLay = (LinearLayout) findViewById(R.id.title_lay);
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setImageResource(R.drawable.search);
		triangleDownImg.setVisibility(View.VISIBLE);
		mShoplist_onelist1 = (ListView) findViewById(R.id.Shoplist_onelist1);
		mShoplist_twolist1 = (ListView) findViewById(R.id.Shoplist_twolist1);
		classificationLinear = (LinearLayout) findViewById(R.id.classification_linear);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.PullToRefreshListView);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		if (examAdapter == null) {
			examAdapter = new ExamAdapter(ExamActivity.this, null,null);
		}
		pullToRefreshListView.setAdapter(examAdapter);
		pullToRefreshListView.setVisibility(View.VISIBLE);
		tvSearchNull.setVisibility(View.GONE);
		ivRight.setOnClickListener(this);
		upDateListView(0);

	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void clickRight() {
		// tag 值大于 0 ，  代表在线考试，点击跳入搜索，    tag<0, 代表 等级考试， 点击跳入等级考试页面，  tag = 0 表示全部
		if(ExamActivity.tag>=0){    
			Intent inten = new Intent(ExamActivity.this, TitleSearchAct.class);
			inten.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, Category.CATEGORY_EXAM);
			inten.putExtra(TitleSearchAct.SEARCH_TAG, ExamActivity.tag);
			startActivity(inten);
		}else{
			Intent inten = new Intent(ExamActivity.this, MyRatingActivity.class);
			startActivity(inten);
		}
		overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
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
			clickRight();
			break;
		default:
			break;
		}
	}
	
	private class Onelistclick1 implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mainListClickPosition = arg2;
			classificationsTemp = classifications.get(arg2).getClassifications();
			if(classificationsTemp == null){
				classificationsTemp = new ArrayList<Classification>();
			}
			initAdapter1(classificationsTemp);
			oneadapter1.setSelectItem(arg2);
			oneadapter1.notifyDataSetChanged();
			if(classificationsTemp == null || classificationsTemp.size()==0){
				ExamActivity.tag = classifications.get(arg2).getTag();
				String title = classifications.get(arg2).getName();
				ExamActivity.CLASSIFICATIONNAME = title;
				tvTitle.setText(title);
				tvSearchNull.setVisibility(View.GONE);
				classificationLinear.setVisibility(View.GONE);
				beginIndex = 0;
				upDateListView(0);
				SP.putIntSP(ExamActivity.this, SP.EXAM, "main", mainListClickPosition);
				SP.putIntSP(ExamActivity.this, SP.EXAM, "more", 0);
			}
			if(ExamActivity.tag>=0){
				ivRight.setImageResource(R.drawable.search);
			}else{
				ivRight.setImageResource(R.drawable.view_ratings);
			}
		}
	}
	
	private class Twolistclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String title = classificationsTemp.get(arg2).getName();
			ExamActivity.tag = classificationsTemp.get(arg2).getTag();
			ExamActivity.CLASSIFICATIONNAME = title;
			tvTitle.setText(title);
			twoadapter1.setSelectItem(arg2);
			twoadapter1.notifyDataSetChanged();
			tvSearchNull.setVisibility(View.GONE);
			classificationLinear.setVisibility(View.GONE);
			beginIndex = 0;
			upDateListView(0);
			SP.putIntSP(ExamActivity.this, SP.EXAM, "main", mainListClickPosition);
			SP.putIntSP(ExamActivity.this, SP.EXAM, "more", arg2);
			if(ExamActivity.tag>=0){
				ivRight.setImageResource(R.drawable.search);
			}else{
				ivRight.setImageResource(R.drawable.view_ratings);
			}
		}
	}
	
	private void initAdapter1(ArrayList<Classification> classifications) {
		twoadapter1 = new SearchMoreAdapter(ExamActivity.this, classifications,R.layout.shop_list2_item);
		mShoplist_twolist1.setAdapter(twoadapter1);
		twoadapter1.notifyDataSetChanged();
	}
	
	/**
	 * 功能描述:通过网络获取 类别 列表
	 */
	private void getClassifications() {
		ServiceProvider.doGetCategorys(ExamActivity.this, Category.CATEGORY_EXAM , new VolleyListener(ExamActivity.this) {
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(ExamActivity.this);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				JSONArray resultArray = response.optJSONArray(Net.DATA);
				// 缓存分类信息
				SP.putStringSP(ExamActivity.this, SP.EXAM, Exam.CATEGORY_EXAM, resultArray.toString());
				setClassifications(resultArray);
			}
		});
	}
	
	/**
	 * @param resultArray
	 * 解析jsonArray
	 */
	private void setClassifications(JSONArray resultArray){
		classifications = new ArrayList<Classification>();
		if (resultArray != null && resultArray.length() != 0) {
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Classification category = new Classification(ExamActivity.this,jsonObject,SP.EXAM);
				classifications.add(category);
			}
		}
		oneadapter1 = new SearchMainAdapter(ExamActivity.this, classifications,R.layout.shop_list1_item);
		oneadapter1.setSelectItem(0);
		mShoplist_onelist1.setAdapter(oneadapter1);
		ArrayList<Classification> classificationsFirst = classifications.get(0).getClassifications();
		if(classificationsFirst!=null&&classificationsFirst.size()>0){
			initAdapter1(classificationsFirst);
		}
		// 如果没有二级分类的话，只显示左边的一栏
		if(!Classification.isHasErjiClassification){
			mShoplist_twolist1.setVisibility(View.GONE);
		}
	}
	
	
	/**
	 * 初始化item点击监听
	 */
	private void initListener() {
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				BackWebActivity.PAGEFLAG = BackWebActivity.EXAM;
				Exam exam = examAdapter.getItem(position - 1);
				int subtype = exam.getType();
				if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
					return;
				}
				// 考试没有联网
				if(ToastUtil.showNetExc(ExamActivity.this)){
					return;
				}
				String uid = ((AppApplication) ExamActivity.this.getApplicationContext()).getUserInfo().getUserId();
				String url =  Net.getRunHost(ExamActivity.this)+Net.EXAM_ITEM(uid, exam.getExamId());
				Intent intents = new Intent(ExamActivity.this, BackWebActivity.class);
				intents.putExtra(BackWebActivity.VALUE_URL,url);
				intents.putExtra(BackWebActivity.ISSHOWTONGJI, true);
				int tag = exam.getTag();
				String title = "";
				if(tag>=0){
					// 获取分类名
					title = SP.getStringSP(ExamActivity.this, SP.EXAM, tag+"", "");
				}else{
					title = ExamActivity.CLASSIFICATIONNAME;
				}
				intents.putExtra(BackWebActivity.VALUE_TITLE,title); 
				startActivity(intents);
				// 设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCash(ExamActivity.tag);
		beginIndex = 0;
		upDateListView(0);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		upDateListView(beginIndex);
	}

	private void upDateListView(final int beginNum) {
		if(TextUtils.isEmpty(Category.CLICKMAINICON)){
			return;
		} 
		getExam(beginNum);
	}
	
	private void getExam(final int beginNum){
		updatePro.setVisibility(View.VISIBLE);
		int getNum = 0;
		String getType = "exam";
		if(ExamActivity.tag>=0){
			getNum = Constant.LIST_ITEM_NUM;
			getType = "exam";
		}else{
			getNum = 100;
			getType = "rank";
		}
		if(beginIndex==0&&list!=null&&list.size()>0){
			list.clear();
		}
		pullToRefreshListView.setVisibility(View.VISIBLE);
		ServiceProvider.doUpdateLocalResource2(ExamActivity.this, getType,
				Math.abs(ExamActivity.tag), beginNum, getNum, "",null,
				new VolleyListener(ExamActivity.this) {
					@Override
					public void onResponse(Object responseObject) {
						updatePro.setVisibility(View.GONE);
						getDataFromJsonObject(responseObject,beginNum);
					}

					@Override
					public void onErrorResponse(VolleyError arg0) {
						super.onErrorResponse(arg0);
						pullToRefreshListView.onRefreshComplete();
						updatePro.setVisibility(View.GONE);
					}
				});
	}
	
	private void getDataFromJsonObject(Object responseObject,int beginNum){
		list = new ArrayList<Exam>();
		JSONObject responseJson = (JSONObject) responseObject;
		try {
			JSONObject data = responseJson
					.optJSONObject(Net.DATA);
			if (null == data || "".equals(data)) {
				return;
			}
			JSONArray resultArray = data.optJSONArray(Net.LIST);
			if (null == resultArray
					|| resultArray.length() <= 0) {
				if(beginIndex>0){
					ToastUtil.showUpdateToast(ExamActivity.this);
				}else{
					tvSearchNull.setVisibility(View.VISIBLE);
					pullToRefreshListView.setVisibility(View.GONE);
				}
				return;
			}
			// 保存未读计数
			if (tag == 0) {
				SP.putIntSP(ExamActivity.this,SP.DEFAULTCACHE, userNum+Exam.UNREAD_NUM_EXAM,data.optInt(ResponseParams.NEWCNT));
			}
			//添加缓存
			if(beginIndex == 0){
				SP.putStringSP(ExamActivity.this,SP.EXAM, userNum+ExamActivity.tag, resultArray.toString());
			}else{
				String SPJSONArray =  SP.getStringSP(ExamActivity.this,SP.EXAM, userNum+ExamActivity.tag, "");
				Exam.putSPJsonArray(ExamActivity.this, ExamActivity.tag+"",userNum, SPJSONArray, resultArray);
			}
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Exam entity = new Exam(jsonObject);
				list.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				beginIndex = resultArray.length();
				examAdapter.setDatas(list);
			} else {
				examAdapter.addData(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pullToRefreshListView.onRefreshComplete();
		}
	}
	
	/**
	 * 功能描述:  获取缓存
	 */
	public void getCash(int tag){
		ArrayList<Exam> list = new ArrayList<Exam>();
		userNum = ((AppApplication) ExamActivity.this.getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(ExamActivity.this,SP.EXAM, userNum+ExamActivity.tag, "");
		if(ToastUtil.showNetExc(ExamActivity.this)){
			if(TextUtils.isEmpty(sp)){
				examAdapter.setDatas(list);
				tvSearchNull.setVisibility(View.VISIBLE);
				return;
			}else{
				tvSearchNull.setVisibility(View.GONE);
			}
		}
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(sp);
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Exam entity = new Exam(jsonObject);
				list.add(entity);
			}
			examAdapter.setDatas(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 以免该值被下次重用，所以在这里还原一下
		Classification.isHasErjiClassification = false;
		ExamActivity.tag = 0;
		SP.putIntSP(ExamActivity.this, SP.EXAM, "main", 0);
		SP.putIntSP(ExamActivity.this, SP.EXAM, "more", 0);
	}
}

