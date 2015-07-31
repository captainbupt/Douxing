package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.StoreAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.StoreListPresenter;
import com.badou.mworking.view.StoreListView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoreActivity extends BaseBackActionBarActivity implements StoreListView {

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    StoreAdapter mStoreAdapter;

    StoreListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(R.string.user_center_my_store);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);
        initListener();
        mPresenter = (StoreListPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new StoreListPresenter(mContext);
    }

    private void initListener() {
        mStoreAdapter = new StoreAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteStore(mStoreAdapter.getItem((Integer) v.getTag()), (Integer) v.getTag());
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.praiseStore(mStoreAdapter.getItem((Integer) v.getTag()), (Integer) v.getTag());
            }
        });
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onItemClick((Store) adapterView.getAdapter().getItem(i), i - 1);
            }
        });
        mContentListView.setAdapter(mStoreAdapter);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
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
        showProgressBar();
    }

    @Override
    public boolean isRefreshing() {
        return mContentListView.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mContentListView.onRefreshComplete();
        hideProgressBar();
    }

    @Override
    public void setData(List<Store> data) {
        mStoreAdapter.setList(data);
    }

    @Override
    public void addData(List<Store> data) {
        mStoreAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mStoreAdapter.getCount();
    }

    @Override
    public void setItem(int index, Store item) {
        mStoreAdapter.setItem(index, item);
    }

    @Override
    public Store getItem(int index) {
        return mStoreAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mStoreAdapter.remove(index);
    }
}
