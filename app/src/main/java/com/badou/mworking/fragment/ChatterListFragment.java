package com.badou.mworking.fragment;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.ChatterActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterAdapter;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 功能描述: 同事圈列表页
 */
public class ChatterListFragment extends BaseFragment {

    public static final int REQUEST_CHATTER_DETAIL = 1;
    public static final String KEY_ARGUMENT_TOPIC = "topic";

    private int mClickPostion = -1;
    private int mCurrentPage = 1;// 当前页码

    private ChatterAdapter mChatterAdapter;
    private PullToRefreshListView mContentListView;
    private ImageView mNoneResultImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, null);
        mContentListView = (PullToRefreshListView) view
                .findViewById(R.id.ptrlv_fragment_chatter_list);
        mContentListView.setMode(Mode.BOTH);
        mNoneResultImageView = (ImageView) view.findViewById(R.id.iv_fragment_chatter_list_none_result);
        initListener();
        initData();
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
        mChatterAdapter = new ChatterAdapter(mContext, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickPostion = position - 1;
                // 跳转到单条的Item的页面，并传递数据
                Chatter chatter = (Chatter) mChatterAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, ChatterDetailActivity.class);
                intent.putExtra(ChatterDetailActivity.VALUE_QUESTION, chatter);
                startActivityForResult(intent, REQUEST_CHATTER_DETAIL);
            }
        });
        mContentListView.setAdapter(mChatterAdapter);
    }

    private void initData() {
        getCache();
        mCurrentPage = 1;
        mContentListView.setRefreshing();
    }

    /**
     * 功能描述:滚动到最底加载更多
     */
    private void updateData(final int beginNum) {
        ((BaseActionBarActivity) mActivity).showProgressBar();
        Bundle mReceivedArguments = getArguments();
        String topic = mReceivedArguments == null ? null : mReceivedArguments.getString(KEY_ARGUMENT_TOPIC);
        // 发起网络请求
        ServiceProvider.doQuestionShareList(mContext, "share", topic, beginNum,
                Constant.LIST_ITEM_NUM, new VolleyListener(getActivity()) {
                    @Override
                    public void onResponse(Object responseObject) {
                        if (!mActivity.isFinishing()) {
                            ((BaseActionBarActivity) getActivity()).hideProgressBar();
                        }
                        mContentListView.onRefreshComplete();
                        JSONObject response = (JSONObject) responseObject;
                        int code = response.optInt(Net.CODE);
                        if (code == Net.LOGOUT) {
                            AppApplication.logoutShow(mContext);
                            return;
                        }
                        if (code != Net.SUCCESS) {
                            return;
                        }
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
                            } else {
                                mNoneResultImageView.setVisibility(View.VISIBLE);
                                mChatterAdapter.setList(null);
                            }
                            return;
                        }
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
                            SP.putStringSP(mContext, SP.TONGSHIQUAN, userNum + Chatter.QUESTIONCACHE, resultArray.toString());
                            mChatterAdapter.setList(chatters);
                        } else {// 继续加载
                            mChatterAdapter.addList(chatters);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        super.onErrorResponse(arg0);
                        if (!mActivity.isFinishing()) {
                            ((ChatterActivity) getActivity()).hideProgressBar();
                        }
                        mContentListView.onRefreshComplete();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (mChatterAdapter.getCount() >= mClickPostion) {
                Chatter chatter = (Chatter) mChatterAdapter.getItem(mClickPostion);
                chatter.replyNumber = (data.getIntExtra(ChatterDetailActivity.RESPONSE_KEY_RELAY_NUMBER, 0));
                mChatterAdapter.setItem(mClickPostion, chatter);
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
        String sp = SP.getStringSP(getActivity(), SP.TONGSHIQUAN, userNum + Chatter.QUESTIONCACHE, "");
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
