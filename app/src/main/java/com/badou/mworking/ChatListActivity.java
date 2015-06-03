package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ChatAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.ChattingListInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 功能描述: 聊天页面
 */
public class ChatListActivity extends BaseBackActionBarActivity {

    public static final String KEY_HEAD_URL = "KEY_HEAD_URL";
    private PullToRefreshListView pullListView;
    private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        setActionbarTitle(getResources().getString(R.string.user_center_message));
        initView();
        initListener();
        pullListView.setRefreshing();
        //getChatList();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullToRefreshListView) findViewById(R.id.ptrlv_activity_chat_list);
        mAdapter = new ChatAdapter(mContext);
        pullListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatList();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
                                    long arg3) {
                Intent intent = new Intent(mContext, ChattingActivity.class);
                ChattingListInfo contanctsList = (ChattingListInfo) mAdapter.getItem(positon - 1);
                intent.putExtra(ChattingActivity.KEY_NAME, contanctsList.name);
                intent.putExtra(ChattingActivity.KEY_WHOM, contanctsList.whom);
                intent.putExtra(ChattingActivity.KEY_OTHER_IMG, contanctsList.img);
                intent.putExtra(ChattingActivity.KEY_SELF_IMG, mReceivedIntent.getStringExtra(KEY_HEAD_URL));
                startActivity(intent);
            }
        });

        // 设置回调函数
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getChatList();
            }
        });
    }


    /**
     * 获取聊天列表
     */
    private void getChatList() {
        ServiceProvider.dogetChatList(mContext, new VolleyListener(mContext) {
            @Override
            public void onResponse(Object responseObject) {
                pullListView.onRefreshComplete();
                JSONObject responseJson = (JSONObject) responseObject;
                List<Object> chatList = new ArrayList<>();
                int code = responseJson.optInt(Net.CODE);
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (Net.SUCCESS != code) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                JSONArray dataArr = responseJson.optJSONArray(Net.DATA);
                if (dataArr == null || dataArr.length() == 0) {
                    ToastUtil.showToast(mContext,
                            R.string.result_upate_null);
                } else {
                    for (int i = 0; i < dataArr.length(); i++) {
                        JSONObject chatJson = dataArr.optJSONObject(i);
                        if (chatJson == null) {
                            return;
                        }
                        ChattingListInfo chat = new ChattingListInfo(chatJson);
                        chatList.add(chat);
                    }
                    mAdapter.setList(chatList);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pullListView.onRefreshComplete();
            }
        });
    }
}
