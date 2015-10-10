package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badou.mworking.adapter.StoreAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.StoreListPresenter;
import com.badou.mworking.view.StoreListView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.NoneResultView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class StoreActivity extends BaseBackActionBarActivity implements StoreListView {

    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
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
                int position = (int) v.getTag();
                mPresenter.onItemClick(mStoreAdapter.getItem(position), position);
            }
        }, new View.OnClickListener() {
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
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(new DividerItemDecoration(mContext));
        mContentListView.setAdapter(mStoreAdapter);
        mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                mPresenter.loadMore();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.refresh();
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
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    @Override
    public void enablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mPtrClassicFrameLayout.autoRefresh();
        showProgressBar();
    }

    @Override
    public boolean isRefreshing() {
        return mPtrClassicFrameLayout.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mPtrClassicFrameLayout.refreshComplete();
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
        return mStoreAdapter.getItemCount();
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
