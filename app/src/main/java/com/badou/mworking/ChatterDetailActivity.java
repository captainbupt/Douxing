package com.badou.mworking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.listener.DeleteClickListener;
import com.badou.mworking.listener.MessageClickListener;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.ChatterDetailPresenter;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.ChatterDetailView;
import com.badou.mworking.widget.BottomSendMessageView;
import com.badou.mworking.widget.ChatterItemView;
import com.badou.mworking.widget.MultiImageShowGridView;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.NoScrollListView.OnNoScrollItemClickListener;
import com.badou.mworking.widget.NoneResultView;
import com.badou.mworking.widget.TextViewFixTouchConsume;
import com.badou.mworking.widget.VideoImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能描述: 同事圈详情
 */
public class ChatterDetailActivity extends BaseBackActionBarActivity implements ChatterDetailView {

    private static final String KEY_QID = "qid";

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.bottom_send_view)
    BottomSendMessageView mBottomSendView;

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
        mChatterItemView = new ChatterItemView(mContext);
        mContentListView.getRefreshableView().addHeaderView(mChatterItemView, null, false);
        mNoneResultView = new NoneResultView(mContext);
        mNoneResultView.setContent(-1, R.string.none_result_reply);
        mNoneResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        mNoneResultView.setPadding(0, DensityUtil.getInstance().getOffsetXlarge(), 0, 0);
    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    protected void initListener() {
        mChatterItemView.setOnDeletedListener(new ChatterItemView.OnDeletedListener() {
            @Override
            public void onDeleted() {
                setResult(RESULT_OK, ListPresenter.getResultIntent(null, true));
                ChatterDetailActivity.super.finish(); // 避免冲突
            }

            @Override
            public void onStart() {
                showProgressDialog(R.string.progress_tips_delete_ing);
            }

            @Override
            public void onComplete() {
                hideProgressDialog();
            }
        });
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatterComment chatterComment = (ChatterComment) parent.getAdapter().getItem(position);
                mPresenter.onItemClick(chatterComment, position - 1);
            }
        });

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
        addStoreImageView(chatter.isStore(), Store.TYPE_STRING_CHATTER, chatter.getQid());
        mReplyAdapter = new CommentAdapter(mContext, chatter.getQid(), chatter.isDeletable(), mProgressDialog);
        mContentListView.setAdapter(mReplyAdapter);
        mChatterItemView.setData(chatter, false, true);
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
        mContentListView.getRefreshableView().addFooterView(mNoneResultView, null, false);
    }

    @Override
    public void hideNoneResult() {
        mContentListView.getRefreshableView().removeFooterView(mNoneResultView);
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
    public void setData(List<Comment> data) {
        mReplyAdapter.setList(data);
    }

    @Override
    public void addData(List<Comment> data) {
        mReplyAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
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
