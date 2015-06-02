package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.category.Exam;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * 历史试题页面
 */
public class HistoryExamActivity extends BaseNoTitleActivity implements OnClickListener,OnRefreshListener2<ListView>{
	
	protected SwipeBackLayout layout;
	private ImageView ivActionbarLeft;
	private TextView  actionbarTitle;
	
	private int beginIndex = 0;

	private PullToRefreshListView pullToRefreshListView; // 下拉刷新listview
	
	private ExamAdapter examAdapter;
	public static List<Object> list;        // 获取到的list集合

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historyexamactivity);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
		initView();
	}
	
	protected void initView(){
		ivActionbarLeft = (ImageView) findViewById(R.id.iv_actionbar_left);
		actionbarTitle = (TextView) findViewById(R.id.txt_actionbar_title);
		ivActionbarLeft.setOnClickListener(this);
		actionbarTitle.setText(getResources().getString(R.string.yikaoshiti_str));
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.ptrlv_user_progress_content);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		if (examAdapter == null) {
			examAdapter = new ExamAdapter(HistoryExamActivity.this);
		}
		pullToRefreshListView.setAdapter(examAdapter);
		pullToRefreshListView.setRefreshing();
		initListener();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_left:
			HistoryExamActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 获取历史考试
	 */
	private void getHistoryExam(int beginIndex){
		ServiceProvider.getPastrank(HistoryExamActivity.this,/*Math.abs(ExamActivity.tag)+""*/"",new VolleyListener(HistoryExamActivity.this) {
			
			@Override
			public void onResponse(Object responseObject) {
				// 在获取在线考试或者等级考试的时候，有beginnum来进行分页获取，而该接口没有，传入0即可
				getDataFromJsonObject(responseObject,0);
			}
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
				super.onErrorResponse(arg0);
				pullToRefreshListView.onRefreshComplete();
			}
		});
	}
	
	private void getDataFromJsonObject(Object responseObject,int beginNum){
		list = new ArrayList<>();
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
					ToastUtil.showUpdateToast(HistoryExamActivity.this);
				}else{
					pullToRefreshListView.setVisibility(View.GONE);
				}
				return;
			}
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Exam entity = new Exam(jsonObject);
				list.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				beginIndex = resultArray.length();
				examAdapter.setList(list);
			} else {
				examAdapter.addList(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pullToRefreshListView.onRefreshComplete();
		}
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		beginIndex = 0;
		getHistoryExam(0);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getHistoryExam(beginIndex);
	}
	
	/**
	 * 初始化item点击监听
	 */
	protected void initListener() {
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				BackWebActivity.PAGEFLAG = BackWebActivity.EXAM;
				Exam exam = (Exam) examAdapter.getItem(position - 1);
				int subtype = exam.subtype;
				if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
					return;
				}
				// 考试没有联网
				if(NetUtils.isNetConnected(mContext)){
					ToastUtil.showNetExc(mContext);
					return;
				}
				String uid = ((AppApplication) HistoryExamActivity.this.getApplicationContext()).getUserInfo().userId;
				String url =  Net.getRunHost(HistoryExamActivity.this)+Net.EXAM_ITEM(uid, exam.rid);
				Intent intents = new Intent(HistoryExamActivity.this, BackWebActivity.class);
				intents.putExtra(BackWebActivity.KEY_URL,url);
				//String title = ExamActivity.CLASSIFICATIONNAME;
				String title = "";
				intents.putExtra(BackWebActivity.KEY_TITLE,title);
				startActivity(intents);
				// 设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}
}
