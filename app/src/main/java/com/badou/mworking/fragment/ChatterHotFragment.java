package com.badou.mworking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterHotAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.chatter.ChatterHot;
import com.badou.mworking.presenter.chatter.ChatterHotPresenter;
import com.badou.mworking.view.BaseListView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterHotFragment extends BaseFragment implements BaseListView<ChatterHot> {

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    ChatterHotAdapter mChatterAdapter;
    ChatterHotPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, null);
        ButterKnife.bind(this, view);
        initialize();
        mPresenter = new ChatterHotPresenter(mContext);
        mPresenter.attachView(this);
        return view;
    }

    private void initialize() {
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.loadMore();
            }
        });
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onItemClick((ChatterHot) adapterView.getAdapter().getItem(i), i - 1);
            }
        });
        mChatterAdapter = new ChatterHotAdapter(mContext);
        mContentListView.setAdapter(mChatterAdapter);
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
        mContentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void enablePullUp() {
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mContentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mContentListView.setRefreshing();
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
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
    public void setData(List data) {
        mChatterAdapter.setList(data);
    }

    @Override
    public void addData(List data) {
        mChatterAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mChatterAdapter.getCount();
    }

    @Override
    public void setItem(int index, ChatterHot item) {
        mChatterAdapter.setItem(index, item);
    }

    @Override
    public ChatterHot getItem(int index) {
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
