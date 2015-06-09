package com.badou.mworking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.AskAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.Ask;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 问答页面
 */
public class AskActivity extends BaseBackActionBarActivity {

    private ImageView mNoneResultImageView;  //当没有人提问时显示该图片

    private PullToRefreshListView mContentListView;
    private AskAdapter mAskAdapter;

    private int beginIndex = 1;
    private int mClickPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mNoneResultImageView = (ImageView) findViewById(R.id.iv_activity_question_none);

        // 因为需要同时添加单击和长按事件，pullToRefresh并不支持该操作。所以只能在adapter里面进行添加
        mContentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_activity_question_content);
    }

    private void initListener() {
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                beginIndex = 1;
                updateListView(beginIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                updateListView(beginIndex);
            }
        });
    }

    private void initData() {
        mContentListView.setMode(Mode.BOTH);
        mAskAdapter = new AskAdapter(mContext);
        getCache();
        mContentListView.setAdapter(mAskAdapter);
        beginIndex = 1;
        mContentListView.setRefreshing();
    }

    @Override
    public void clickRight() {
        super.clickRight();
        /*Intent intent = new Intent(this, PutQuestionActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 功能描述:
     *
     * @param beginNum
     */
    private void updateListView(int beginNum) {
        showProgressBar();
        mNoneResultImageView.setVisibility(View.GONE);
        ServiceProvider.updateAskList(AskActivity.this, beginNum, Constant.LIST_ITEM_NUM, "", new VolleyListener(AskActivity.this) {

            @Override
            public void onResponse(Object responseObject) {
                mContentListView.onRefreshComplete();
                hideProgressBar();
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
                    List<Object> askTemp = new ArrayList<>();
                    int length = data.length();
                    if (length == 0) {
                        if (beginIndex == 1) {
                            mNoneResultImageView.setVisibility(View.VISIBLE);
                            mAskAdapter.setList(null);
                        } else {
                            ToastUtil.showUpdateToast(mContext);
                        }
                        return;
                    }
                    mNoneResultImageView.setVisibility(View.GONE);
                    for (int i = 0; i < length; i++) {
                        JSONObject jb = data.getJSONObject(i);
                        Ask ask = new Ask(jb);
                        askTemp.add(ask);
                    }
                    String userNum = ((AppApplication) getApplicationContext()).getUserInfo().account;
                    //添加缓存
                    if (beginIndex == 1) {
                        //添加缓存
                        SP.putStringSP(AskActivity.this, SP.ASK, userNum + Ask.WENDACACHE, data.toString());
                    } /*else {
                        String SPJSONArray = SP.getStringSP(AskActivity.this, SP.ASK, userNum + Ask.WENDACACHE, "");
                        Ask.putSPJsonArray(AskActivity.this, userNum + Ask.WENDACACHE, SPJSONArray, data);
                    }*/
                    beginIndex++;
                    mAskAdapter.addList(askTemp);
                    mAskAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                hideProgressBar();
                mContentListView.onRefreshComplete();
            }
        });
    }

    /**
     * 功能描述:  获取缓存
     */
    public void getCache() {
        String userNum = ((AppApplication) getApplicationContext()).getUserInfo().account;
        List<Object> list = new ArrayList<>();
        String sp = SP.getStringSP(AskActivity.this, SP.ASK, userNum + Ask.WENDACACHE, "");
        if (TextUtils.isEmpty(sp)) {
            return;
        }
        JSONArray resultArray;
        try {
            resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                Ask ask = new Ask(jsonObject);
                list.add(ask);
            }
            mAskAdapter.addList(list);
            mAskAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}