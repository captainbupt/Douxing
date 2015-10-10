package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.chatter.ChatterListView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.HeaderViewRecyclerAdapter;
import com.badou.mworking.widget.NoneResultView;
import com.badou.mworking.widget.VerticalSpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 功能描述: 同事圈列表页
 */
public class ChatterListFragment extends BaseFragment implements ChatterListView {

    private static final String KEY_ARGUMENT_TOPIC = "topic";
    private static final String KEY_ARGUMENT_UID = "uid";
    private static final String KEY_ARGUMENT_HEAD = "head";
    private static final String KEY_ARGUMENT_LEVEL = "level";

    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    VerticalSpaceItemDecoration mVerticalSpaceItemDecoration;
    HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    ChatterListAdapter mChatterAdapter;
    ChatterListPresenter mPresenter;

    public static ChatterListFragment getTopicFragment(String topic) {
        ChatterListFragment fragment = new ChatterListFragment();
        if (!TextUtils.isEmpty(topic)) {
            Bundle argument = new Bundle();
            argument.putString(KEY_ARGUMENT_TOPIC, topic);
            fragment.setArguments(argument);
        }
        return fragment;
    }

    public static ChatterListFragment getUserFragment(String uid, String head, int level) {
        ChatterListFragment fragment = new ChatterListFragment();
        if (!TextUtils.isEmpty(uid)) {
            Bundle argument = new Bundle();
            argument.putString(KEY_ARGUMENT_UID, uid);
            argument.putString(KEY_ARGUMENT_HEAD, head);
            argument.putInt(KEY_ARGUMENT_LEVEL, level);
            fragment.setArguments(argument);
        }
        return fragment;
    }

    public static ChatterListFragment getFragment() {
        return new ChatterListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chatter_list, container, false);
        ButterKnife.bind(this, view);
        initListener();
        Bundle argument = getArguments();
        if (argument != null && argument.containsKey(KEY_ARGUMENT_TOPIC)) {
            mPresenter = new ChatterListPresenter(mContext, this, argument.getString(KEY_ARGUMENT_TOPIC));
        } else if (argument != null && argument.containsKey(KEY_ARGUMENT_UID)) {
            mPresenter = new ChatterListPresenter(mContext, this, argument.getString(KEY_ARGUMENT_UID), argument.getString(KEY_ARGUMENT_HEAD), argument.getInt(KEY_ARGUMENT_LEVEL, 0));
        } else {
            mPresenter = new ChatterListPresenter(mContext, this);
        }
        mPresenter.attachView(this);
        return view;
    }

    public void setFooterViewNone() {
        mNoneResultView.setVisibility(View.GONE);
        mNoneResultView = new NoneResultView(mContext);
        mNoneResultView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mNoneResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        mNoneResultView.setContent(R.drawable.background_none_result_chatter, R.string.none_result_chatter);
        mNoneResultView.setPadding(0, DensityUtil.getInstance().getOffsetXlarge(), 0, DensityUtil.getInstance().getOffsetXlarge());
        mHeaderViewRecyclerAdapter.addFooterView(mNoneResultView);
    }

    public void setHeaderView(View view) {
        mHeaderViewRecyclerAdapter.addHeaderView(view);
        mContentListView.removeItemDecoration(mVerticalSpaceItemDecoration);
        mVerticalSpaceItemDecoration = new VerticalSpaceItemDecoration(DensityUtil.getInstance().getOffsetLess(), false);
        mContentListView.addItemDecoration(mVerticalSpaceItemDecoration);
    }

    private void initListener() {
        mChatterAdapter = new ChatterListAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mPresenter.onItemClick(mChatterAdapter.getItem(position), position);
            }
        });
        Bundle argument = getArguments();
        if (argument == null || !argument.containsKey(KEY_ARGUMENT_UID)) { // 进入某人同事圈，不需要点击头像事件
            mChatterAdapter.setHeadClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.toUserList(getItem((int) v.getTag()));
                }
            });
        }
        mChatterAdapter.setPraiseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.praise(getItem((int) v.getTag()), (int) v.getTag());
            }
        });
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mVerticalSpaceItemDecoration = new VerticalSpaceItemDecoration(DensityUtil.getInstance().getOffsetLess());
        mContentListView.addItemDecoration(mVerticalSpaceItemDecoration);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mChatterAdapter);
        mContentListView.setAdapter(mHeaderViewRecyclerAdapter);
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
        mNoneResultView.setVisibility(View.GONE);
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
    public void setData(List<Chatter> data) {
        mChatterAdapter.setList(data);
    }

    @Override
    public void addData(List<Chatter> data) {
        mChatterAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mChatterAdapter.getItemCount();
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
