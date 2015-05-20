package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.Net;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 类:  <code> MyExamAct </code>
 * 功能描述: 我的考试
 * 创建人:  葛建锋
 * 创建日期: 2014年12月4日 上午11:03:06
 * 开发环境: JDK7.0
 */
public class MyExamAct extends BaseNoTitleActivity implements OnClickListener,OnRefreshListener2<ListView>{

	public ImageView ivLeft;  //action 左侧iv 
	public ImageView ivRight; 	//action 右侧 iv 
	
	private TextView tvTitle; // action 中间tv
	private TextView tvScore;
	private TextView tvRank;
	private TextView gerenRank;  //个人排名
	
	public static final String VALUE_EXAM = "VALUE_EXAM";
	private UserDetail user;
	
	private PullToRefreshListView pullToRefreshListView; // 下拉刷新listview
	private ExamAdapter examAdapter;
	
	public static List<Object> list;        // 获取到的list集合
	private int beginIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_myexam);
		//页面滑动关闭
		layout.attachToActivity(this);
		initAction(this);
		initView();
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

	/**
	 * c初始化action 布局
	 * @param onclick
	 */
	private void initAction(OnClickListener onclick) {
		tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		gerenRank = (TextView) this.findViewById(R.id.geren_rank);
		ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		ivLeft.setOnClickListener(onclick);
		tvTitle.setText(getResources().getString(R.string.my_exam));
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.PullToRefreshListView);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		if (examAdapter == null) {
			examAdapter = new ExamAdapter(MyExamAct.this, true, false);
		}
		pullToRefreshListView.setAdapter(examAdapter);
		pullToRefreshListView.setRefreshing();
	}

	/**
	 * 初始化控件
	 */
	protected void initView() {
		tvScore = (TextView) this.findViewById(R.id.tv_my_score);
		tvRank = (TextView) this.findViewById(R.id.tv_my_exam_rank);
		user = (UserDetail) getIntent()
				.getSerializableExtra(UserCenterActivity.KEY_USERINFO);
		if (user == null) {
			tvScore.setText(0 + "");
			//return 直接返回，不在进行其他操作，应为这回对空对象操作，下面都会报错
			return;
		} else {
			tvScore.setText(user.getScore() + "");
			
			String str1 =" <font color=\'#ffffff\'><b>"+"第"+"</b></font>";//第
			String str2 =" <font color=\'#ffffff\'><b>"+"名, "+"</b></font>";//名
			String html = str1 + " <font color=\'#f7ab32\'><b>"+user.getScore_over()+ "</b></font>"+ str2;
			
			gerenRank.setText(Html.fromHtml(html));
			setTopTipsText(user);
		}
	}

	/**
	 * 初始化监听
	 */
	protected void initListener() {
		TextView rlGoExam = (TextView) this.findViewById(R.id.comment_relat);
		rlGoExam.setText(MyExamAct.this.getResources().getString(R.string.myexam_btn));
		rlGoExam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Category.CLICKMAINICON = Category.CATEGORY_EXAM;
				Intent goAct = new Intent(MyExamAct.this, ExamActivity.class);
				goAct.putExtra(VALUE_EXAM, 1);
				startActivity(goAct);
			}
		});
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				BackWebActivity.PAGEFLAG = BackWebActivity.EXAM;
				Exam exam = (Exam) examAdapter.getItem(position - 1);
				int subtype = exam.getType();
				if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
					return;
				}
				// 考试没有联网
				if (NetUtils.isNetConnected(mContext)) {
					ToastUtil.showNetExc(mContext);
					return;
				}
				String uid = ((AppApplication) MyExamAct.this.getApplicationContext()).getUserInfo().getUserId();
				String url =  Net.getRunHost(MyExamAct.this)+Net.EXAM_ITEM(uid, exam.getExamId());
				Intent intents = new Intent(MyExamAct.this, BackWebActivity.class);
				intents.putExtra(BackWebActivity.VALUE_URL,url);
				// 获取分类名
				String title = SP.getStringSP(MyExamAct.this, SP.EXAM, exam.getTag()+"", "");
				intents.putExtra(BackWebActivity.VALUE_TITLE,title); 
				startActivity(intents);
				// 设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}
	
	/***设置我的考试,顶部布局文案**/
	private void setTopTipsText(UserDetail user){
		int examRank = user.getScore_rank();
		String str1 =" <font color=\'#ffffff\'><b>"+"超过"+"</b></font>";//你的学习成绩
		String str2 =" <font color=\'#ffffff\'><b>"+"学员"+"</b></font>";//的用户
		String html = str1 + " <font color=\'#f7ab32\'><b>"+examRank+ "%</b></font>"+ str2;
		
		tvRank.setText(Html.fromHtml(html));
	}
	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_actionbar_left:
			finish();
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		beginIndex = 0;
		getExam(0);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getExam(beginIndex);
	}
	
	private void getExam(final int beginNum){
		ServiceProvider.doUpdateLocalResource2(MyExamAct.this, Category.CATEGORY_EXAM,0, beginNum, Constant.LIST_ITEM_NUM, "","1",
				new VolleyListener(MyExamAct.this) {
					@Override
					public void onResponse(Object responseObject) {
						getDataFromJsonObject(responseObject,beginNum);
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
			if (null == resultArray || resultArray.length() <= 0) {
				if(beginIndex>0){
					ToastUtil.showUpdateToast(MyExamAct.this);
				}else{
					pullToRefreshListView.setVisibility(View.GONE);
				}
				return;
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
}
