package com.badou.mworking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.presenter.CommentPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.CommentView;
import com.badou.mworking.widget.BottomSendMessageView;
import com.badou.mworking.widget.CategoryTabContent;
import com.badou.mworking.widget.NoneResultView;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentFragment extends BaseFragment implements CommentView, CategoryTabContent.ScrollableContent {

    public static final String KEY_RID = "rid";

    public static CommentFragment getFragment(String rid) {
        CommentFragment fragment = new CommentFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        fragment.setArguments(argument);
        return fragment;
    }

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;//下拉刷新
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;  // 没有内容时的提示
    @Bind(R.id.bottom_view)
    BottomSendMessageView mBottomView;

    CommentPresenter mPresenter;
    CommentAdapter mCommentAdapter;

    OnCommentCountChangedListener mOnCommentCountChangedListener;

    public interface OnCommentCountChangedListener {
        void onCommentCountChanged(int count);
    }

    public void setOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener) {
        this.mOnCommentCountChangedListener = onCommentCountChangedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        mPresenter = new CommentPresenter(mContext, bundle.getString(KEY_RID));
        mPresenter.attachView(this);
        mCommentAdapter = new CommentAdapter(mContext);
        mContentListView.setAdapter(mCommentAdapter);
        initListener();
        return view;
    }

    public CommentPresenter getPresenter() {
        return mPresenter;
    }

    /**
     * 功能描述:设置监听
     */
    protected void initListener() {
        mBottomView.setOnSubmitListener(new BottomSendMessageView.OnSubmitListener() {
            @Override
            public void onSubmit(String content) {
                mPresenter.submitComment(content);
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

        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Comment question = mCommentAdapter.getItem(position - 1);
                mPresenter.onItemClick(question, position - 1);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
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
        mContentListView.setRefreshing();
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
        mCommentAdapter.setList(data);
    }

    @Override
    public void addData(List data) {
        mCommentAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mCommentAdapter.getCount();
    }

    @Override
    public void setItem(int index, Comment item) {
        mCommentAdapter.setItem(index, item);
    }

    @Override
    public Comment getItem(int index) {
        return mCommentAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mCommentAdapter.remove(index);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {
        hideProgressDialog();
    }

    @Override
    public void setBottomSend() {
        mBottomView.clearContent();
        mBottomView.hideKeyboard();
        mBottomView.setContent(getResources().getString(R.string.comment_hint), getResources().getString(R.string.button_send));
    }

    @Override
    public void setBottomReply(String name) {
        mBottomView.clearContent();
        mBottomView.showKeyboard();
        mBottomView.setContent(getResources().getString(R.string.button_reply) + ": " + name, getResources().getString(R.string.button_reply));
    }

    @Override
    public void setCommentCount(int count) {
        mCommentAdapter.setAllCount(count);
        if (mOnCommentCountChangedListener != null) {
            mOnCommentCountChangedListener.onCommentCountChanged(count);
        }
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, mContentListView.getRefreshableView(), header);
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.entry_comment) + "(" + mCommentAdapter.getAllCount() + ")";
    }

    @Override
    public void onChange(float ratio, float offsetY) {
        ViewHelper.setTranslationY(mBottomView, ViewHelper.getTranslationY(mBottomView) - offsetY);
    }

    boolean isFirst = true;

    @Override
    public void onOffsetCalculated(int offset) {
        if (isFirst) {
            ViewHelper.setTranslationY(mBottomView, -offset);
            isFirst = false;
        }
    }
}
