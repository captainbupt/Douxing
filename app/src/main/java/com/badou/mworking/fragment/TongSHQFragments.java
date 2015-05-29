package com.badou.mworking.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.AroundActivity;
import com.badou.mworking.AroundDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.TongShiQuanAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Question;
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

/**
 * 类: <code> AroundFragment </code> 功能描述: 同事圈列表页 创建人:董奇 创建日期: 2014年7月15日
 * 下午6:27:28 开发环境: JDK7.0
 */
public class TongSHQFragments extends Fragment implements OnRefreshListener2<ListView>{

	public static final String BUNDLE_MODE_USER_KEY = "mode_user";
	public static int clickPostion = -1;
	public static final int requestCode = 2;
    public static final int LOAD_PAGE_NUM = 10;//每页加载的数量
    
	private int mode = 0;
	private int currentPage = 1;// 当前页码
	
	private String uid="";
	private String account = "";
	
	private boolean isUser = false;// 区分 我的圈/同事圈
	public boolean lvIsEnable = true;//listview 的 item 可以点击
	
	private TongShiQuanAdapter aroundAdapter;
	private PullToRefreshListView pullToRefreshListView;
	
	private Context mContext;
	private List<Question> asks;
	
	public ImageView ivLeft;	// action 左侧iv 
	public ImageView ivRight;	//action 右侧 iv 
	public TextView tvTitle; 	// action 中间tv
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
	}

	@Override
	public void onResume() {
		lvIsEnable = true;
		if (Constant.is_refresh) {
			currentPage = 1;
			updateDatas(1,uid);
			Constant.is_refresh = false;
		}
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_group, null);
		uid = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().userId;
		account = ((AppApplication)mContext.getApplicationContext())
				.getUserInfo().account;
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pullListView);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		setAdapterData();
		pullToRefreshListView.setAdapter(aroundAdapter);

		getCash();
		addFooterView();
		currentPage = 1;
		updateDatas(1,uid);
		return view;
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
	public void onDestroy() {
		super.onDestroy();
		Constant.is_refresh = true;
	}

	/**
	 * 功能描述:更新数据
	 */
	private void setAdapterData() {
		if (aroundAdapter == null) {
			aroundAdapter = new TongShiQuanAdapter(getActivity(),this);
		}
	}

	/**
	 * 功能描述:滚动到最底加载更多
	 */
	private void updateDatas(final int page,String uid) {
		((AroundActivity)getActivity()).updatePro.setVisibility(View.VISIBLE);
		String type;
		type = "qas";
		// 发起网络请求
		ServiceProvider.doQuestionShareList(getActivity(), uid,type, page,
				LOAD_PAGE_NUM, isUser, new VolleyListener(getActivity()) {
					@Override
					public void onResponse(Object responseObject) {
						if((AroundActivity)getActivity()!=null){
							((AroundActivity)getActivity()).updatePro.setVisibility(View.GONE);
						}
						pullToRefreshListView.onRefreshComplete();
						JSONObject response = (JSONObject) responseObject;
						JSONObject contentObject = response
								.optJSONObject(Net.DATA);
						if (contentObject == null) {
							ToastUtil.showNetExc(mContext);
							return;
						}

						int code = response.optInt(Net.CODE);
						if (code == Net.LOGOUT) {
							AppApplication.logoutShow(mContext);
							return;
						}
						if (code != Net.SUCCESS) {
							return;
						}
						// 加载到最后时 提示无更新
						JSONArray resultArray = contentObject
								.optJSONArray(Net.RESULT);
						if (resultArray == null || resultArray.length() == 0) {
							ToastUtil.showNetExc(mContext);
						} else {
							currentPage++;
							// 新加载的内容添加到list
							asks = new ArrayList<Question>();
							for (int i = 0; i < resultArray.length(); i++) {
								JSONObject jo2 = resultArray.optJSONObject(i);
								if (jo2 == null) {
									return;
								}
								asks.add(new Question(jo2, mode));
							}
							if (page == 1) {// 页码为1 重新加载第一页
								aroundAdapter.setDatas(asks);
								currentPage = 1;
							} else {// 继续加载
								aroundAdapter.addDatas(asks);
							}
							if(mContext!=null){
								//添加缓存
								if(page == 1){
									//添加缓存
									SP.putStringSP(mContext,SP.TONGSHIQUAN, account +Question.QUESTIONCACHE, resultArray.toString());
								}else{
									String SPJSONArray =  SP.getStringSP(mContext,SP.TONGSHIQUAN, account +Question.QUESTIONCACHE, "");
									Question.putSPJsonArray(mContext, account +Question.QUESTIONCACHE, SPJSONArray, resultArray);
								}
							}
						}
						aroundAdapter.notifyDataSetChanged();
					}

					@Override
					public void onErrorResponse(VolleyError arg0) {
						super.onErrorResponse(arg0);
						pullToRefreshListView.onRefreshComplete();
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (aroundAdapter.getCount()>=clickPostion ) {
				aroundAdapter.getItem(clickPostion).setReply_no(data.getIntExtra(AroundDetailActivity.KEY_RELAY_NO,0));
				aroundAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 功能描述:  获取缓存
	 */
	public void getCash(){
		ArrayList<Question> list = new ArrayList<Question>();
		String sp = SP.getStringSP(getActivity(),SP.TONGSHIQUAN, account +Question.QUESTIONCACHE, "");
		if(TextUtils.isEmpty(sp)){
			return;
		}
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(sp);
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Question question = new Question(jsonObject,mode);
				list.add(question);
			}
			aroundAdapter.setDatas(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 这里刷新listview数据,只加载第一页的数据
		updateDatas(1,uid);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		updateDatas(currentPage + 1,uid);
	}
}
