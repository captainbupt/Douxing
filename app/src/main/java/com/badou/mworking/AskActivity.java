package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        setRightText(R.string.ask_title_right);
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
        //updateListView(1);
        mContentListView.setRefreshing();
    }

    @Override
    public void clickRight() {
        super.clickRight();
        Intent intent = new Intent(this, AskSubmitActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AskDetailActivity.RESULT_DELETED) {
            mAskAdapter.remove(mClickPosition);
        } else if (resultCode == AskDetailActivity.RESULT_REPLIED) {
            Ask ask = (Ask) mAskAdapter.getItem(mClickPosition);
            ask.count = data.getIntExtra(AskDetailActivity.RESULT_KEY_COUNT, ask.count);
            mAskAdapter.setItem(mClickPosition, ask);
        }
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
            public void onCompleted() {
                hideProgressBar();
                mContentListView.onRefreshComplete();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONArray data = response.optJSONArray(Net.DATA);
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
                    JSONObject jb = data.optJSONObject(i);
                    Ask ask = new Ask(jb);
                    askTemp.add(ask);
                }
                String userNum = ((AppApplication) getApplicationContext()).getUserInfo().account;
                //添加缓存
                if (beginIndex == 1) {
                    mAskAdapter.setList(askTemp);
                    //添加缓存
                    SP.putStringSP(AskActivity.this, SP.ASK, userNum + Ask.WENDACACHE, data.toString());
                } else {
                    mAskAdapter.addList(askTemp);
                }
                beginIndex++;
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
            mAskAdapter.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
