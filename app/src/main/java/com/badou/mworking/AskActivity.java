package com.badou.mworking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.AskAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.badou.mworking.listener.AdapterItemLongClickListener;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskPresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.ask.AskListView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答页面
 */
public class AskActivity extends BaseBackActionBarActivity implements AskListView {

    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;

    AskAdapter mAskAdapter;

    AskPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, Shuffle.BUTTON_ASK).getName());
        setContentView(R.layout.activity_ask);
        ButterKnife.bind(this);
        initView();
        mPresenter = (AskPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new AskPresenter(mContext);
    }

    private void initView() {
        setRightText(R.string.ask_title_right, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.publishAsk();
            }
        });
        mContentListView.setMode(Mode.BOTH);
        // 单点和长按会冲突，只能在adapter里面加
        mAskAdapter = new AskAdapter(mContext, new AdapterItemClickListener(mContext) {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.tag_position);
                mPresenter.onItemClick(mAskAdapter.getItem(position), position);
            }
        }, new AdapterItemLongClickListener(mContext) {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.copy(mAskAdapter.getItem((int) v.getTag(R.id.tag_position)));
                return true;
            }
        });
        mContentListView.setAdapter(mAskAdapter);
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
        super.onActivityResult(requestCode, resultCode, data);
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
        showProgressBar();
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
        hideProgressBar();
    }

    @Override
    public void setData(List<Ask> data) {
        mAskAdapter.setList(data);
    }

    @Override
    public void addData(List<Ask> data) {
        mAskAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mAskAdapter.getCount();
    }

    @Override
    public void setItem(int index, Ask item) {
        mAskAdapter.setItem(index, item);
    }

    @Override
    public Ask getItem(int index) {
        return mAskAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mAskAdapter.remove(index);
    }

}
