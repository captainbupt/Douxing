package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.ChatterActivity;
import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterHotAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.ChatterHot;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/9.
 */
public class ChatterHotFragment extends BaseFragment {

    private int mCurrentPage = 1;// 当前页码

    private ChatterHotAdapter mChatterAdapter;
    private PullToRefreshListView mContentListView;
    private NoneResultView mNoneResultImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, null);
        mContentListView = (PullToRefreshListView) view
                .findViewById(R.id.ptrlv_fragment_chatter_list);
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
        mNoneResultImageView = (NoneResultView) view.findViewById(R.id.nrv_fragment_chatter_list_none_result);
        initListener();
        initData();
        return view;
    }

    private void initListener() {
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatterHot hot = (ChatterHot) mChatterAdapter.getItem(i - 1);
                Intent intent = new Intent(mContext, ChatterUserActivity.class);
                intent.putExtra(ChatterUserActivity.KEY_UID, hot.uid);
                startActivity(intent);
            }
        });
        mChatterAdapter = new ChatterHotAdapter(mContext);
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
        // 发起网络请求
        ServiceProvider.doGetChatterHot(mContext, beginNum, Constant.LIST_ITEM_NUM,
                new VolleyListener(getActivity()) {

                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing()) {
                            ((ChatterActivity) getActivity()).hideProgressBar();
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
                                .optJSONArray(Net.LIST);
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
                            chatters.add(new ChatterHot(jo2));
                        }

                        if (beginNum == 1) {// 页码为1 重新加载第一页
                            final String userNum = ((AppApplication) mContext.getApplicationContext())
                                    .getUserInfo().account;
                            SP.putStringSP(mContext, SP.CHATTERHOT, userNum, resultArray.toString());
                            mChatterAdapter.setList(chatters);
                        } else {// 继续加载
                            mChatterAdapter.addList(chatters);
                        }
                    }
                });
    }

    /**
     * 功能描述:  获取缓存
     */
    public void getCache() {
        final String userNum = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().account;
        ArrayList<Object> list = new ArrayList<>();
        String sp = SP.getStringSP(getActivity(), SP.CHATTERHOT, userNum, "");
        if (TextUtils.isEmpty(sp)) {
            return;
        }
        JSONArray resultArray;
        try {
            resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                ChatterHot question = new ChatterHot(jsonObject);
                list.add(question);
            }
            mChatterAdapter.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
