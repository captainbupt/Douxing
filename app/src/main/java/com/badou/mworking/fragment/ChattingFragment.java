package com.badou.mworking.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.badou.mworking.ChatInfoActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.ContanctsList;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

/**
 * 类:  <code> ChattingFragment </code>
 * 功能描述: 聊天列表fragmnet
 * 创建人:  葛建锋
 * 创建日期: 2014年9月19日 上午10:20:12
 * 开发环境: JDK7.0
 */
public class ChattingFragment extends Fragment{

	public static String KEY_HEAD_URL = "KEY_HEAD_URL";
	public static String KEY_SP_UNREAD_NUM = "chatUnread";
	private Context mContext;
	private XListView pullListView;
	private ChatAdapter mAdapter;
	private ArrayList<ContanctsList> chatList;
	private String myHeadImgUrl = "";
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		KEY_HEAD_URL = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserId();
		getUserInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat, null);
		initView(view);
		initListener();
		getChatList();
		return view;
	}

	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SP.putStringSP(mContext, SP.DEFAULTCACHE,ChattingFragment.KEY_HEAD_URL, "");
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	public ArrayList<ContanctsList> getListData() {
		if (chatList == null ) {
			chatList = new ArrayList<ContanctsList>();
		}
		return chatList;
	}
	
	/**
	 * 初始化控件
	 * @param view
	 */
	private void initView(View view){
		pullListView = (XListView) view.findViewById(R.id.PullToRefreshListView);
		chatList = new ArrayList<ContanctsList>();
		setAdapterData();
		pullListView.setAdapter(mAdapter);
		
		// 设置回调函数
		pullListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				getChatList();
			}

			@Override
			public void onLoadMore() {
			}
		});
	}
	
	/**
	 * 初始化监听
	 */
	private void initListener() {
		pullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
					long arg3) {
				Intent intent = new Intent(mContext, ChatInfoActivity.class);
				intent.putExtra(ChatInfoActivity.KEY_NAME, chatList.get(positon-1).getName());
				intent.putExtra(ChatInfoActivity.KEY_whom, chatList.get(positon-1).getWhom());
				intent.putExtra(ChatInfoActivity.KEY_img, chatList.get(positon -1).getImg());
				intent.putExtra(ChattingFragment.KEY_HEAD_URL, myHeadImgUrl);
				startActivity(intent);
			}
		});
	}
	
	
	/**
	 *  获取聊天列表
	 */
	private void getChatList(){
		ServiceProvider.dogetChatList(mContext, new VolleyListener(mContext) {
			@Override
			public void onResponse(Object responseObject) {
				if(pullListView!=null){
					pullListView.stopRefresh();
				}
				JSONObject responseJson = (JSONObject) responseObject;
				chatList = new ArrayList<ContanctsList>();
				int code = responseJson.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(mContext);
					return;
				}
				if (Net.SUCCESS != code) {
					ToastUtil.showNetExc(mContext);
					return ;
				}
				JSONArray dataArr = responseJson.optJSONArray(Net.DATA);
				if (dataArr == null || dataArr.length() == 0) {
					ToastUtil.showToast(getActivity(),
							R.string.result_upate_null);
				}else {
					SP.putIntSP(mContext,SP.DEFAULTCACHE, KEY_SP_UNREAD_NUM, 0);
					for (int i = 0; i < dataArr.length(); i++) {
						JSONObject chatJson = dataArr.optJSONObject(i);
						if (chatJson == null) {
							return;
						}
						ContanctsList chat = new ContanctsList(chatJson);
						chatList.add(chat);
						saveTotalUnread(chat.getMsgcnt());
					}
					setAdapterData();
				}
			}
			@Override
			public void onErrorResponse(VolleyError error) {
				super.onErrorResponse(error);
				if(pullListView!=null){
					pullListView.stopRefresh();
				}
			}
		});
	}
	
	/**设置更新mAdapter **/
	private void setAdapterData(){
		if (mAdapter == null ) {
			mAdapter = new ChatAdapter(mContext, chatList);
		} else {
			mAdapter.setData(chatList);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 
	 * 功能描述:获取用户的头像信息保存到sp中
	 */
	private void getUserInfo() {
		String uid = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserId();
		String headUrl = SP.getStringSP(mContext,SP.DEFAULTCACHE, KEY_HEAD_URL, "");
		if (headUrl != null && !headUrl.equals("")) {
			myHeadImgUrl = headUrl ;
		} else {
			ServiceProvider.doOptainUserDetail(mContext, uid,new VolleyListener(
					mContext) {
				@Override
				public void onErrorResponse(VolleyError error) {
					super.onErrorResponse(error);
				}

				@Override
				public void onResponse(Object arg0) {
					JSONObject jsonObject = (JSONObject) arg0;
					int errcode = jsonObject.optInt(Net.CODE);
					if (errcode != 0) {
						return;
					}
					JSONObject jObject = null;
					try {
						jObject = new JSONObject(jsonObject.optString(Net.DATA));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (jObject == null) {
						return;
					}
					UserDetail userDetail = new UserDetail(jObject);
					SP.putStringSP(mContext,SP.DEFAULTCACHE, KEY_HEAD_URL, userDetail.getHeadimg());
				}
			});
		}
	}
	
	
	public void saveTotalUnread(int value) {
		String userNum = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserNumber();
		int afterValue = SP.getIntSP(mContext, SP.DEFAULTCACHE,"chatUnread", 0);
		afterValue = afterValue + value; 
		SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum+KEY_SP_UNREAD_NUM, afterValue);
	}
}
