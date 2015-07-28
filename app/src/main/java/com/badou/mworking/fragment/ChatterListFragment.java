package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterListAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.presenter.chatter.ChatterListPresenter;
import com.badou.mworking.view.BaseListView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能描述: 同事圈列表页
 */
public class ChatterListFragment extends BaseFragment implements BaseListView<Chatter> {

    private static final String KEY_ARGUMENT_TOPIC = "topic";

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    ChatterListAdapter mChatterAdapter;
    ChatterListPresenter mPresenter;

    public static ChatterListFragment getFragment(String topic) {
        ChatterListFragment fragment = new ChatterListFragment();
        if (!TextUtils.isEmpty(topic)) {
            Bundle argument = new Bundle();
            argument.putString(KEY_ARGUMENT_TOPIC, topic);
            fragment.setArguments(argument);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, null);
        ButterKnife.bind(this, view);
        initListener();
        Bundle argument = getArguments();
        if (argument != null && argument.containsKey(KEY_ARGUMENT_TOPIC)) {
            mPresenter = new ChatterListPresenter(mContext, this, argument.getString(KEY_ARGUMENT_TOPIC));
        } else {
            mPresenter = new ChatterListPresenter(mContext, this);
        }
        mPresenter.attachView(this);
        return view;
    }

    private void initListener() {
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onItemClick((Chatter) adapterView.getAdapter().getItem(i), i - 1);
            }
        });
        mChatterAdapter = new ChatterListAdapter(mContext, true);
        mContentListView.setAdapter(mChatterAdapter);
        mContentListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.loadMore();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showNoneResult() {
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disablePullUp() {
        mContentListView.setMode(Mode.PULL_FROM_START);
    }

    @Override
    public void enablePullUp() {
        mContentListView.setMode(Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mContentListView.setMode(Mode.PULL_FROM_START);
        mContentListView.setRefreshing();
        mContentListView.setMode(Mode.BOTH);
    }

    @Override
    public boolean isRefreshing() {
        return mContentListView.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mContentListView.onRefreshComplete();
    }

    @Override
    public void setData(List<Chatter> data) {
        mChatterAdapter.setList(data);
    }

    @Override
    public void addData(List<Chatter> data) {
        mChatterAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mChatterAdapter.getCount();
    }

    @Override
    public void setItem(int index, Chatter item) {
        mChatterAdapter.setItem(index, item);
    }

    @Override
    public Chatter getItem(int index) {
        return mChatterAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mChatterAdapter.remove(index);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }
}
