package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badou.mworking.adapter.AskAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.ask.AskListView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.NoneResultView;
import com.badou.mworking.widget.VerticalSpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 问答页面
 */
public class AskActivity extends BaseBackActionBarActivity implements AskListView {

    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;

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
        // 单点和长按会冲突，只能在adapter里面加
        mAskAdapter = new AskAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mPresenter.onItemClick(mAskAdapter.getItem(position), position);
            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.copy(mAskAdapter.getItem((int) v.getTag(R.id.tag_position)));
                return true;
            }
        });
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(new VerticalSpaceItemDecoration(DensityUtil.getInstance().getOffsetLess()));
        mContentListView.setAdapter(mAskAdapter);
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
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    @Override
    public void enablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        showProgressBar();
        mPtrClassicFrameLayout.autoRefresh();
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
    public void setData(List<Ask> data) {
        mAskAdapter.setList(data);
    }

    @Override
    public void addData(List<Ask> data) {
        mAskAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mAskAdapter.getItemCount();
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
