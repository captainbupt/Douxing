package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.chatter.ChatterDetailPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.chatter.ChatterDetailView;
import com.badou.mworking.widget.BottomSendMessageView;
import com.badou.mworking.widget.ChatterItemView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.HeaderViewRecyclerAdapter;
import com.badou.mworking.widget.NoneResultView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 功能描述: 同事圈详情
 */
public class ChatterDetailActivity extends BaseBackActionBarActivity implements ChatterDetailView {

    private static final String KEY_QID = "qid";

    @Bind(R.id.bottom_send_view)
    BottomSendMessageView mBottomSendView;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;

    ImageView mStoreImageView;

    CommentAdapter mReplyAdapter;// 同事圈list
    NoneResultView mNoneResultView;
    ChatterItemView mChatterItemView;
    ChatterDetailPresenter mPresenter;

    public static Intent getIntent(Context context, String qid) {
        Intent intent = new Intent(context, ChatterDetailActivity.class);
        intent.putExtra(KEY_QID, qid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_NeiRongXiangQing));
        setContentView(R.layout.activity_chatter_detail);
        ButterKnife.bind(this);
        initView();
        initListener();
        mPresenter = (ChatterDetailPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new ChatterDetailPresenter(mContext, mReceivedIntent.getStringExtra(KEY_QID));
    }

    /**
     * 功能描述:实例化自定义listview,设置显示的内容
     */
    protected void initView() {
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(new DividerItemDecoration(mContext));
        mChatterItemView = new ChatterItemView(mContext);
        mChatterItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mNoneResultView = new NoneResultView(mContext);
        mChatterItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mNoneResultView.setContent(-1, R.string.none_result_reply);
        mNoneResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        mNoneResultView.setPadding(0, DensityUtil.getInstance().getOffsetXlarge(), 0, 0);
    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    protected void initListener() {
        mChatterItemView.setMessageListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toMessage();
            }
        });
        mChatterItemView.setDeleteListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteChatter();
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

        mBottomSendView.setOnSubmitListener(new BottomSendMessageView.OnSubmitListener() {
            @Override
            public void onSubmit(String content) {
                mPresenter.submitComment(content);
            }
        });
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, mPresenter.getResult());
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.onBackPressed())
            super.onBackPressed();
    }

    @Override
    public void setData(Chatter chatter) {
        mStoreImageView = getDefaultImageView(mContext, chatter.isStore() ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
        addTitleRightView(mStoreImageView, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStoreClicked();
            }
        });
        mReplyAdapter = new CommentAdapter(mContext, chatter.getQid(), chatter.isDeletable(), new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                ChatterComment chatterComment = (ChatterComment) mReplyAdapter.getItem(position);
                mPresenter.onItemClick(chatterComment, position);
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteChatterReply((int) v.getTag());
            }
        });
        HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mReplyAdapter);
        mChatterItemView.setData(chatter, true, 0);
        headerViewRecyclerAdapter.addHeaderView(mChatterItemView);
        headerViewRecyclerAdapter.addFooterView(mNoneResultView);
        mContentListView.setAdapter(headerViewRecyclerAdapter);
    }

    @Override
    public void setStore(boolean isStore) {
        mStoreImageView.setImageResource(isStore ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
    }

    @Override
    public void setBottomSend() {
        mBottomSendView.clearContent();
        mBottomSendView.hideKeyboard();
        mBottomSendView.setContent(getResources().getString(R.string.comment_hint), getResources().getString(R.string.button_send));
    }

    @Override
    public void setBottomReply(String name) {
        mBottomSendView.clearContent();
        mBottomSendView.showKeyboard();
        mBottomSendView.setContent(getResources().getString(R.string.button_reply) + ": " + name, getResources().getString(R.string.button_reply));
    }

    @Override
    public void setCommentCount(int count) {
        mReplyAdapter.setAllCount(count);
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
    public void setData(List<Comment> data) {
        mReplyAdapter.setList(data);
    }

    @Override
    public void addData(List<Comment> data) {
        mReplyAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mReplyAdapter.getItemCount();
    }

    @Override
    public int getAllCount() {
        return mReplyAdapter.getAllCount();
    }

    @Override
    public void setItem(int index, Comment item) {
        mReplyAdapter.setItem(index, item);
    }

    @Override
    public Comment getItem(int index) {
        return mReplyAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mReplyAdapter.remove(index);
    }
}
