package com.badou.mworking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.WenDAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Ask;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 问答页面
 */
public class WenDActivity extends BaseNoTitleActivity implements OnClickListener,OnRefreshListener2<ListView>{
	
	private ImageView actionbarLeftImg;
	private ImageView actionbarRightImg;
	private RelativeLayout wendaNoproblemImg;  //当没有人提问时显示该图片
	private TextView  actionbarTitleTv;
	private TextView commentTv; //我要提问

	private PullToRefreshListView pullToRefreshListView;
	private WenDAdapter wenDAdapter;
	
	private ArrayList<Ask> asks = new ArrayList<Ask>(); 
	private Ask ask = null;  //跳转传入的ask对象
	
	private int beginIndex = 1;
	
	public static Boolean ISREBOOLEANWENDALIST = false;      //是否刷新问答列表
	public static Boolean ISDELETE = false;
	
	private String userNum = "";
	
	private ProgressBar updatePro; // 刷新进度条
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wendactivity);
		//页面滑动关闭
		layout.attachToActivity(this);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(WenDActivity.ISREBOOLEANWENDALIST){
			WenDActivity.ISREBOOLEANWENDALIST = false;
			beginIndex = 1;
			upDateListView(1);
		}
		//如果删除的话，更新数据
		if(WenDActivity.ISDELETE){
			WenDActivity.ISDELETE = false;
			asks.remove(ask);
			wenDAdapter.notifyDataSetChanged();
			if(asks!=null&&asks.size()==0){
				pullToRefreshListView.setVisibility(View.GONE);
				wendaNoproblemImg.setVisibility(View.VISIBLE);
			}
		}
		if(ask!=null&&WenDaDetailActivity.ANSWEARCOUNT!=ask.getCount()){
			ask.setCount(WenDaDetailActivity.ANSWEARCOUNT);
			wenDAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	private void init(){
		userNum = ((AppApplication)getApplicationContext())
				.getUserInfo().getUserNumber();
		updatePro = (ProgressBar) findViewById(R.id.pb_action_bar);
		actionbarLeftImg = (ImageView) findViewById(R.id.iv_actionbar_left);
		actionbarTitleTv = (TextView) findViewById(R.id.txt_actionbar_title);
		actionbarRightImg = (ImageView) findViewById(R.id.iv_actionbar_right);
		wendaNoproblemImg = (RelativeLayout) findViewById(R.id.wenda_noproblem_ralay);
		commentTv = (TextView) findViewById(R.id.comment_relat);
		commentTv.setText("我要提问");
		commentTv.setOnClickListener(this);
		actionbarRightImg.setVisibility(View.VISIBLE);
		actionbarRightImg.setImageResource(R.drawable.search);
		actionbarLeftImg.setOnClickListener(this);
		actionbarRightImg.setOnClickListener(this);
		String titleName = getIntent().getStringExtra(BaseActionBarActivity.KEY_TITLE);
		if(!TextUtils.isEmpty(titleName)){
			actionbarTitleTv.setText(titleName);
		}
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullListView);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		
		wenDAdapter = new WenDAdapter(WenDActivity.this,asks);
		pullToRefreshListView.setAdapter(wenDAdapter);
		addFooterView();
		getCash();
		upDateListView(1);
	}
	
	/** 
	 *  添加底部布局，避免遮挡最后一条
	 */
	private void addFooterView(){
		ListView lv = pullToRefreshListView.getRefreshableView();
		View lastView = new View(mContext);
		AbsListView.LayoutParams vLp = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, getResources()
						.getDimensionPixelOffset(
								R.dimen.my_examAndStudy_lvFooter_h));
		lastView.setLayoutParams(vLp);
		lv.addFooterView(lastView, null, false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_left:
			finish();
			break;
		case R.id.iv_actionbar_right:
			Intent intent = new Intent(this, WenDaSearchActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
			break;
		case R.id.comment_relat:
			Intent intent1 = new Intent(this, PutQuestionActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
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

	/**
	 * 功能描述:
	 * @param beginNum
	 */
	private void upDateListView(int beginNum) {
		updatePro.setVisibility(View.VISIBLE);
		ServiceProvider.updateWendaList(WenDActivity.this, beginNum, Constant.LIST_ITEM_NUM, "", new VolleyListener(WenDActivity.this) {
			
			@Override
			public void onResponse(Object responseObject) {
				pullToRefreshListView.onRefreshComplete();
				updatePro.setVisibility(View.GONE);
				JSONObject responseJson = (JSONObject) responseObject;
				int code = responseJson.optInt(Net.CODE);
				if (code == Net.LOGOUT) {
					AppApplication.logoutShow(mContext);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				try {
					JSONArray data = responseJson.getJSONArray(Net.DATA);
					ArrayList<Ask> askTemp = new ArrayList<Ask>();
					int length = data.length();
					if(length==0){
						if(beginIndex==1){
							pullToRefreshListView.setVisibility(View.GONE);
							wendaNoproblemImg.setVisibility(View.VISIBLE);
						}else{
							wendaNoproblemImg.setVisibility(View.GONE);
							ToastUtil.showToast(WenDActivity.this, "没有更多了");
						}
						return;
					}
					wendaNoproblemImg.setVisibility(View.GONE);
					pullToRefreshListView.setVisibility(View.VISIBLE);
					for (int i = 0; i < length; i++) {
						JSONObject jb = data.getJSONObject(i);
						Ask ask = new Ask(jb);
						askTemp.add(ask);
					}
					//添加缓存
					if(beginIndex == 1){
						asks.clear();
						//添加缓存
						SP.putStringSP(WenDActivity.this,SP.WENDA, userNum+Ask.WENDACACHE, data.toString());
					}else{
						String SPJSONArray =  SP.getStringSP(WenDActivity.this,SP.WENDA, userNum+Ask.WENDACACHE, "");
						Ask.putSPJsonArray(WenDActivity.this, userNum+Ask.WENDACACHE, SPJSONArray, data);
					}
					beginIndex++;
					asks.addAll(askTemp);
					wenDAdapter.notifyDataSetChanged();
					pullToRefreshListView.onRefreshComplete();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onErrorResponse(VolleyError error) {
				super.onErrorResponse(error);
				beginIndex = 1;
				pullToRefreshListView.onRefreshComplete();
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		beginIndex = 1;
		upDateListView(beginIndex);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		upDateListView(beginIndex);
	}
	
	/**
	 * 功能描述:  获取缓存
	 */
	public void getCash(){
		ArrayList<Ask> list = new ArrayList<Ask>();
		String sp = SP.getStringSP(WenDActivity.this,SP.WENDA, userNum+Ask.WENDACACHE, "");    
		if(TextUtils.isEmpty(sp)){
			return;
		}
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(sp);
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Ask ask = new Ask(jsonObject);
				list.add(ask);
			}
			asks.addAll(list);
			wenDAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
