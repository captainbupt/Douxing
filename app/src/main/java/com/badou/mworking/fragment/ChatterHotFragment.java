package com.badou.mworking.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.badou.mworking.view.chatter.ChatterHotListView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.NoneResultView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class ChatterHotFragment extends BaseFragment implements ChatterHotListView {

    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
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
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(new DividerItemDecoration(mContext));
        mChatterAdapter = new ChatterHotAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mPresenter.onItemClick(mChatterAdapter.getItem(position), position - 1);
            }
        });
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
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    @Override
    public void enablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mPtrClassicFrameLayout.autoRefresh();
    }

    @Override
    public boolean isRefreshing() {
        return mPtrClassicFrameLayout.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mPtrClassicFrameLayout.refreshComplete();
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
        return mChatterAdapter.getItemCount();
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
