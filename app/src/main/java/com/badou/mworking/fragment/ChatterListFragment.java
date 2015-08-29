package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
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
public class ChatterListFragment extends BaseFragment implements ChatterListView {

    private static final String KEY_ARGUMENT_TOPIC = "topic";
    private static final String KEY_ARGUMENT_UID = "uid";
    private static final String KEY_ARGUMENT_HEAD = "head";
    private static final String KEY_ARGUMENT_LEVEL = "level";

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    int mHeaderCount = 0;
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
        mContentListView.getRefreshableView().addFooterView(mNoneResultView, null, false);
    }

    public void setHeaderView(View view) {
        mContentListView.getRefreshableView().addHeaderView(view, null, false);
        mHeaderCount++;
    }

    private void initListener() {
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onItemClick((Chatter) adapterView.getAdapter().getItem(i), i - 1 - mHeaderCount);
            }
        });
        mChatterAdapter = new ChatterListAdapter(mContext);
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
