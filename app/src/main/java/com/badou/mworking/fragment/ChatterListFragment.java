package com.badou.mworking.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.ChatterActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.ChatterTopicActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterListAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 同事圈列表页
 */
public class ChatterListFragment extends BaseFragment {

    public static final int REQUEST_CHATTER_DETAIL = 1;
    public static final String KEY_ARGUMENT_TOPIC = "topic";

    private int mClickPosition = -1;
    private int mCurrentPage = 1;// 当前页码

    private ChatterListAdapter mChatterAdapter;
    private PullToRefreshListView mContentListView;
    private NoneResultView mNoneResultView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, null);
        mContentListView = (PullToRefreshListView) view
                .findViewById(R.id.ptrlv_fragment_chatter_list);
        mContentListView.setMode(Mode.BOTH);
        mNoneResultView = (NoneResultView) view.findViewById(R.id.nrv_fragment_chatter_list_none_result);

        mNoneResultView.setContent(R.drawable.background_none_result_chatter, R.string.none_result_chatter);
        initListener();
        getCache();
        refreshData();
        return view;
    }

    private void initListener() {
        mContentListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 这里刷新listview数据,只加载第一页的数据
                mCurrentPage = 1;
                updateData(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                updateData(mCurrentPage);
            }
        });
        mChatterAdapter = new ChatterListAdapter(mContext, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickPosition = position;
                // 跳转到单条的Item的页面，并传递数据
                Chatter chatter = (Chatter) mChatterAdapter.getItem(position);
                Intent intent = new Intent(mContext, ChatterDetailActivity.class);
                intent.putExtra(ChatterDetailActivity.KEY_CHATTER, chatter);
                startActivityForResult(intent, REQUEST_CHATTER_DETAIL);
            }
        }, true);
        mContentListView.setAdapter(mChatterAdapter);
    }

    public void refreshData() {
        mCurrentPage = 1;
        mContentListView.setRefreshing();
    }

    /**
     * 功能描述:滚动到最底加载更多
     */
    private void updateData(final int beginNum) {
        ((BaseActionBarActivity) mActivity).showProgressBar();
        Bundle mReceivedArguments = getArguments();
        final String topic = mReceivedArguments == null ? null : mReceivedArguments.getString(KEY_ARGUMENT_TOPIC);
        // 发起网络请求
        ServiceProvider.doQuestionShareList(mContext, "share", topic, beginNum,
                Constant.LIST_ITEM_NUM, new VolleyListener(getActivity()) {

                    @Override
                    public void onErrorCode(int code) {
                        mNoneResultView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        if (mActivity.getClass().equals(ChatterActivity.class) && !mActivity.isFinishing()) {
                            ((ChatterActivity) getActivity()).hideProgressBar();
                        }
                        if (mActivity.getClass().equals(ChatterTopicActivity.class) && !mActivity.isFinishing()) {
                            ((ChatterTopicActivity) getActivity()).hideProgressBar();
                        }
                        mContentListView.onRefreshComplete();
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        JSONObject contentObject = response
                                .optJSONObject(Net.DATA);
                        if (contentObject == null) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        // 加载到最后时 提示无更新
                        JSONArray resultArray = contentObject
                                .optJSONArray(Net.RESULT);
                        if (resultArray == null || resultArray.length() == 0) {
                            if (beginNum > 1) {
                                ToastUtil.showUpdateToast(mContext);
                                mNoneResultView.setVisibility(View.GONE);
                            } else {
                                mNoneResultView.setVisibility(View.VISIBLE);
                                mChatterAdapter.setList(null);
                            }
                            return;
                        }
                        mNoneResultView.setVisibility(View.GONE);
                        mCurrentPage++;
                        // 新加载的内容添加到list
                        List<Object> chatters = new ArrayList<>();
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jo2 = resultArray.optJSONObject(i);
                            chatters.add(new Chatter(jo2));
                        }
                        if (beginNum == 1) {// 页码为1 重新加载第一页
                            final String userNum = ((AppApplication) mContext.getApplicationContext())
                                    .getUserInfo().account;
                            SP.putStringSP(mContext, SP.CHATTER, userNum + topic, resultArray.toString());
                            mChatterAdapter.setList(chatters);
                        } else {// 继续加载
                            mChatterAdapter.addList(chatters);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mClickPosition >= 0 && mClickPosition < mChatterAdapter.getCount()) {
            if (resultCode == Activity.RESULT_OK) {
                Chatter chatter = (Chatter) mChatterAdapter.getItem(mClickPosition);
                if (data != null) {
                    chatter.replyNumber = (data.getIntExtra(ChatterDetailActivity.RESULT_KEY_COUNT, -1));
                } else {
                    chatter.replyNumber = -1;
                }
                if (chatter.replyNumber < 0) {
                    mChatterAdapter.remove(mClickPosition);
                } else {
                    mChatterAdapter.setItem(mClickPosition, chatter);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 功能描述:  获取缓存
     */
    public void getCache() {
        final String userNum = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().account;
        ArrayList<Object> list = new ArrayList<>();
        Bundle mReceivedArguments = getArguments();
        final String topic = mReceivedArguments == null ? null : mReceivedArguments.getString(KEY_ARGUMENT_TOPIC);
        String sp = SP.getStringSP(getActivity(), SP.CHATTER, userNum + topic, "");
        if (TextUtils.isEmpty(sp)) {
            return;
        }
        JSONArray resultArray;
        try {
            resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                Chatter question = new Chatter(jsonObject);
                list.add(question);
            }
            mChatterAdapter.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
