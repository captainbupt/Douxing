package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.Task;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
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

/**
 * @author gejianfeng
 * 任务签到界面
 */
public class TaskActivity extends BaseNoTitleActivity{// implements OnClickListener,OnRefreshListener2<ListView>{
/*
	private static final int LISTVIEW_RESULT_REFRESH = 0x00001;

	private int beginIndex = 0;

	public ArrayList<Task> list;
	public int clickPosition;
	
	private String userNum = "";
	
	private ImageView tvSearchNull;
	private TaskAdapter taskAdapter;
	private PullToRefreshListView pullToRefreshListView;
	
	private SearchMainAdapter oneadapter1 = null;
	private SearchMoreAdapter twoadapter1 = null;
	private ArrayList<Classification> classifications = new ArrayList<Classification>();
	private ArrayList<Classification> classificationsTemp = new ArrayList<Classification>();
	
	private ImageView ivLeft;  //action 左侧iv
	private ImageView triangleDownImg;   //下拉的图标
	private ImageView ivRight;  	//action 右侧 iv
	private TextView tvTitle;  //action 中间tv
	private ListView mShoplist_onelist1;
	private ListView mShoplist_twolist1;
	private LinearLayout titleLay;  // title 的布局
	private LinearLayout classificationLinear;  // 下拉布局
	
	private static int tag = 0;
	private int mainListClickPosition = 0;
	
	private ProgressBar updatePro; // 刷新进度条
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_name_notice);
		TaskActivity.tag = 0;
		initAction();
		//页面滑动关闭
		layout.attachToActivity(this);
		//设置title及tab文字
		ivRight.setVisibility(View.VISIBLE);
		ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		tvTitle.setText(getIntent().getStringExtra(MainGridActivity.KEY_TITLE_NAME));
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
						int main = SP.getIntSP(TaskActivity.this, SP.TASK, "main", 0);
						int more = SP.getIntSP(TaskActivity.this, SP.TASK, "more", 0);
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
					Animation anim = AnimationUtils.loadAnimation(TaskActivity.this, R.anim.popup_enter);
					classificationLinear.startAnimation(anim);
				}else{
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_down);
					classificationLinear.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(TaskActivity.this, R.anim.popup_exit);
					classificationLinear.startAnimation(anim);
				}
			}
		});
		initListener();
		getClassifications();
		if (NetUtils.isNetConnected(mContext)) {
			ToastUtil.showNetExc(mContext);
			String classificationStr =  SP.getStringSP(TaskActivity.this, SP.TASK,Task.CATEGORY_TASK, "");
			try {
				JSONArray jsonArray = new JSONArray(classificationStr);
				setClassifications(jsonArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		getCash(TaskActivity.tag);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		try {
			if(SignActivity.isSignSuccess){
				SignActivity.isSignSuccess = false;
				if(list!=null&&list.size()>0){
					list.get(clickPosition).setRead(Constant.FINISH_YES);
					taskAdapter.notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	*//**
	 * c初始化action 布局
	 *//*
	private void initAction(){
		updatePro = (ProgressBar) findViewById(R.id.pb_action_bar);
		tvSearchNull = (ImageView) this.findViewById(R.id.tv_tishi);
		ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		ivLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		triangleDownImg = (ImageView) findViewById(R.id.iv_action_bar_triangle);
		titleLay = (LinearLayout) findViewById(R.id.ll_action_bar_title);
		mShoplist_onelist1 = (ListView) findViewById(R.id.Shoplist_onelist1);
		mShoplist_twolist1 = (ListView) findViewById(R.id.Shoplist_twolist1);
		classificationLinear = (LinearLayout) findViewById(R.id.classification_linear);
		pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.PullToRefreshListView);
		tvTitle.setText("");
		ivRight.setVisibility(View.VISIBLE);
		triangleDownImg.setVisibility(View.VISIBLE);
		ivRight.setImageResource(R.drawable.search);
		ivRight.setOnClickListener(this);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setVisibility(View.VISIBLE);
		tvSearchNull.setVisibility(View.GONE);
		if (taskAdapter == null) {
			taskAdapter = new TaskAdapter(mContext, null);
		}
		pullToRefreshListView.setAdapter(taskAdapter);
		upDateListView(0);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	public void clickRight() {
		Intent inten = new Intent(TaskActivity.this, TitleSearchAct.class);
		inten.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, Category.CATEGORY_TASK);
		inten.putExtra(TitleSearchAct.SEARCH_TAG, TaskActivity.tag);
		startActivity(inten);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
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
				TaskActivity.tag = classifications.get(arg2).getTag();
				String title = classifications.get(arg2).getName();
				tvTitle.setText(title);
				tvSearchNull.setVisibility(View.GONE);
				classificationLinear.setVisibility(View.GONE);
				beginIndex = 0;
				upDateListView(0);
				SP.putIntSP(TaskActivity.this, SP.TASK, "main", mainListClickPosition);
				SP.putIntSP(TaskActivity.this, SP.TASK, "more", 0);
			}
		}
	}
	
	private class Twolistclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String title = classificationsTemp.get(arg2).getName();
			TaskActivity.tag = classificationsTemp.get(arg2).getTag();
			tvTitle.setText(title);
			twoadapter1.setSelectItem(arg2);
			twoadapter1.notifyDataSetChanged();
			classificationLinear.setVisibility(View.GONE);
			tvSearchNull.setVisibility(View.GONE);
			beginIndex = 0;
			upDateListView(0);
			SP.putIntSP(TaskActivity.this, SP.TASK, "main", mainListClickPosition);
			SP.putIntSP(TaskActivity.this, SP.TASK, "more", arg2);
		}
	}
	
	private void initAdapter1(ArrayList<Classification> classifications) {
		twoadapter1 = new SearchMoreAdapter(TaskActivity.this, classifications,R.layout.shop_list2_item);
		mShoplist_twolist1.setAdapter(twoadapter1);
		twoadapter1.notifyDataSetChanged();
	}
	
	*//**
	 * 功能描述:通过网络获取 类别 列表
	 *//*
	private void getClassifications() {
		ServiceProvider.doGetCategorys(TaskActivity.this, Category.CATEGORY_TASK , new VolleyListener(TaskActivity.this) {
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(TaskActivity.this);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				JSONArray resultArray = response.optJSONArray(Net.DATA);
				// 缓存分类信息
				SP.putStringSP(TaskActivity.this, SP.TASK, Task.CATEGORY_TASK, resultArray.toString());
				setClassifications(resultArray);
			}
		});
	}
	
	*//**
	 * @param resultArray
	 * 解析jsonArray
	 *//*
	private void setClassifications(JSONArray resultArray){
		classifications = new ArrayList<Classification>();
		if (resultArray != null && resultArray.length() != 0) {
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Classification category = new Classification(TaskActivity.this,jsonObject,SP.TASK);
				classifications.add(category);
			}
		}
		oneadapter1 = new SearchMainAdapter(TaskActivity.this, classifications,R.layout.shop_list1_item);
		oneadapter1.setSelectItem(0);
		mShoplist_onelist1.setAdapter(oneadapter1);
		ArrayList<Classification> classificationsFirst = classifications.get(0).getClassifications();
		if(classificationsFirst!=null&&classificationsFirst.size()>0){
			initAdapter1(classificationsFirst);
		}
		// 如果没有二级分类的话，只显示左边的一栏
		if(!Classification.hasErjiClassification){
			mShoplist_twolist1.setVisibility(View.GONE);
		}
	}
	
	*//**
	 * 初始化监听
	 *//*
	protected void initListener(){
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				clickPosition = position - 1;
				// 获取点中的item所对应的task，并将其作为参数传递给下一个activity
				Task task = taskAdapter.getItem(position - 1);
				int subtype = task.getSubtype();
				if(Constant.MWKG_FORAMT_TYPE_XML != subtype){
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable(SignActivity.INTENT_TASK, task);
				Intent intent = new Intent(mContext, SignActivity.class);
				intent.putExtra(SignActivity.INTENT_TASK, bundle);
				
				// 获取分类名
				String title = SP.getStringSP(TaskActivity.this, SP.TASK, task.getTag()+"", "");
    			intent.putExtra("title", title);
				startActivity(intent);
				//设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}
	
	*//**
	 * 功能描述:
	 * @param beginNum
	 *//*
	private void upDateListView(final int beginNum) {
		updatePro.setVisibility(View.VISIBLE);
		userNum = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserNumber();
		pullToRefreshListView.setVisibility(View.VISIBLE);
		if(beginIndex==0&&list!=null&&list.size()>0){
			list.clear();
		}
		ServiceProvider.doUpdateLocalResource2(mContext, Task.CATEGORY_TASK, TaskActivity.tag, 
				beginNum, Constant.LIST_ITEM_NUM, "", null,new VolleyListener(
						mContext) {

					@Override
					public void onResponse(Object responseObject) {
						list = new ArrayList<Task>();
						JSONObject responseJson = (JSONObject) responseObject;
						updatePro.setVisibility(View.GONE);
						try {
							JSONObject data = responseJson.optJSONObject(Net.DATA);
							if (null == data || "".equals(data)) {
								return;
							}
							JSONArray resultArray = data.optJSONArray(Net.LIST);
							if (null == resultArray || resultArray.length() <= 0) {
								if(beginIndex>0){
									ToastUtil.showUpdateToast(mContext);
								}else{
									tvSearchNull.setVisibility(View.VISIBLE);
									pullToRefreshListView.setVisibility(View.GONE);
								}
								return;
							}
							if (tag == 0) {
								SP.putIntSP(mContext,SP.DEFAULTCACHE, userNum+Task.UNREAD_NUM_TASK, data.optInt(ResponseParams.NEWCNT));
							}
							//添加缓存
							if(beginIndex == 0){
								//添加缓存
								SP.putStringSP(mContext,SP.TASK, userNum+TaskActivity.tag, resultArray.toString());
							}else{
								String SPJSONArray =  SP.getStringSP(mContext,SP.TASK, userNum+TaskActivity.tag, "");
								Task.putSPJsonArray(mContext, TaskActivity.tag+"", userNum, SPJSONArray, resultArray);
							}
							for (int i = 0 ; i < resultArray.length(); i++) {
								JSONObject jsonObject = resultArray
										.optJSONObject(i);
								Task entity = new Task(jsonObject);
								list.add(entity);
								beginIndex++;
							}
							if (beginNum <= 0) {
								beginIndex = resultArray.length();
								taskAdapter.setDatas(list);
							} else {
								taskAdapter.addData(list);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							pullToRefreshListView.onRefreshComplete();
						}
					}

					@Override
					public void onErrorResponse(VolleyError arg0) {
						System.out.println(arg0.getMessage()+"");
						super.onErrorResponse(arg0);
						pullToRefreshListView.onRefreshComplete();
						updatePro.setVisibility(View.GONE);
					}
				});

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

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 10) {
			handler.obtainMessage(LISTVIEW_RESULT_REFRESH, data).sendToTarget();
		}
	}
	
	final Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LISTVIEW_RESULT_REFRESH:
				Intent data = (Intent) msg.obj;
				int pos = data.getIntExtra(Task.TASK_FRAGMENT_ITEM_POSITION, -1);
				if (taskAdapter!=null) {
					if (pos > -1 & pos <= taskAdapter.getCount()) {
						taskAdapter.changeItem(pos, (Task)data.getBundleExtra(Task.SIGN_BACK_TASK_FRAGMENT).getSerializable(Task.SIGN_BACK_TASK_FRAGMENT));
					}
				}
				break;
			default:
				break;
			}
		};
	};
	
	*//**
	 * 功能描述:  获取缓存
	 *//*
	public void getCash(int tag){
		ArrayList<Task> list = new ArrayList<Task>();
		userNum = ((AppApplication) mContext.getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(mContext,SP.TASK, userNum+TaskActivity.tag, "");
		if(NetUtils.isNetConnected(mContext)){
			ToastUtil.showNetExc(mContext);
			if(TextUtils.isEmpty(sp)){
				taskAdapter.setDatas(list);
				tvSearchNull.setVisibility(View.VISIBLE);
				return;
			}else{
				tvSearchNull.setVisibility(View.GONE);
			}
		}
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(sp);
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Task entity = new Task(jsonObject);
				list.add(entity);
			}
			taskAdapter.setDatas(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 以免该值被下次重用，所以在这里还原一下
		Classification.hasErjiClassification = false;
		TaskActivity.tag = 0;
		SP.putIntSP(TaskActivity.this, SP.TASK, "main", 0);
		SP.putIntSP(TaskActivity.this, SP.TASK, "more", 0);
	}*/
}

