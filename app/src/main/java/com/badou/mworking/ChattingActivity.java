/* 
 * 文件名: ChatInfoActivity.java
 * 包路径: com.badou.mworking
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年9月18日 下午3:47:22
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年9月18日 下午3:47:22 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.os.Bundle;
import android.widget.ListView;

import com.badou.mworking.adapter.ChatInfoAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.ChatInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomSendMessageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 聊天页面
 */
public class ChattingActivity extends BaseBackActionBarActivity {

    private BottomSendMessageView mBottomView;
    private PullToRefreshListView mContentListView;
    private String whom = "";
    private String img = "";
    private String mHeadImgUrl = "";
    private ChatInfoAdapter mAdapter;
    public static final String KEY_WHOM = "whom";
    public static final String KEY_OTHER_IMG = "img";
    public static final String KEY_NAME = "name";
    public static final String KEY_SELF_IMG = "myimg";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_chatting);
        whom = getIntent().getStringExtra(KEY_WHOM);
        img = getIntent().getStringExtra(KEY_OTHER_IMG);
        mHeadImgUrl = getIntent().getStringExtra(KEY_SELF_IMG);
        initView();
        initListener();
        mContentListView.setRefreshing();
    }

    /**
     * 功能描述: 控件初始化
     */
    private void initView() {
        mContentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_activity_chatting);
        mContentListView.setMode(Mode.PULL_FROM_START);
        mBottomView = (BottomSendMessageView) findViewById(R.id.bsmv_activity_chatting);
        mAdapter = new ChatInfoAdapter(mContext, whom, img,
                mHeadImgUrl);
        mContentListView.setAdapter(mAdapter);
    }

    protected void initListener() {
        mContentListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                updateChatMsg();
            }
        });
        mBottomView.setOnSubmitListener(new BottomSendMessageView.OnSubmitListener() {
            @Override
            public void onSubmit(String content) {
                submitChatMsg(content);
            }
        });
    }

    private void submitChatMsg(final String content) {

        ServiceProvider.doSendChat(mContext, content, whom, new VolleyListener(
                mContext) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setContent(content);
                chatInfo.setTs(System.currentTimeMillis() / 1000);
                mAdapter.addItem(chatInfo);
                /** 发送成功后滚动到底部 **/
                ListView lv = mContentListView.getRefreshableView();
                lv.setSelection(lv.getBottom());
            }
        });
    }

    private void updateChatMsg() {
        ServiceProvider.dogetChatInfo(mContext, whom,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        if (mContentListView != null) {
                            mContentListView.onRefreshComplete();
                        }
                        JSONObject responseJson = (JSONObject) responseObject;
                        int code = responseJson.optInt(Net.CODE);
                        if (code == Net.LOGOUT) {
                            AppApplication.logoutShow(mContext);
                            return;
                        }
                        if (Net.SUCCESS != code) {
                            ToastUtil.showToast(mContext, R.string.error_service);
                            return;
                        }
                        JSONArray arrJson = responseJson
                                .optJSONArray(Net.DATA);
                        List<Object> chatInfoList = new ArrayList<>();
                        try {
                            for (int i = 0; i < arrJson.length(); i++) {

                                String jo = (String) arrJson.get(i);
                                JSONObject jsonObject = new JSONObject(
                                        jo);
                                chatInfoList.add(new ChatInfo(
                                        jsonObject));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.setList(chatInfoList);
                        /** 发送成功后滚动到底部 **/
                        ListView lv = mContentListView.getRefreshableView();
                        lv.setSelection(lv.getBottom());
                    }

                    @Override
                    public void onCompleted() {
                        if (mContentListView != null) {
                            mContentListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        JSONArray arrJson = response.optJSONArray(Net.DATA);
                        List<Object> chatInfoList = new ArrayList<>();
                        for (int i = 0; i < arrJson.length(); i++) {
                            JSONObject jsonObject = arrJson.optJSONObject(i);
                            chatInfoList.add(new ChatInfo(
                                    jsonObject));
                        }

                        mAdapter.setList(chatInfoList);
                        /** 发送成功后滚动到底部 **/
                        ListView lv = mContentListView.getRefreshableView();
                        lv.setSelection(lv.getBottom());
                    }
                });
    }

}
