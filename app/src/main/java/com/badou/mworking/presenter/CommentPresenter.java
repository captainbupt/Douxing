package com.badou.mworking.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.CommentView;

public abstract class CommentPresenter extends ListPresenter<Comment> {

    protected CommentView mCommentView;

    protected String mWhom = null;
    int mTotalCount = 0;

    public CommentPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mCommentView = (CommentView) v;
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected boolean setData(Object data, int index) {
        CommentOverall<CategoryComment> commentOverall = (CommentOverall<CategoryComment>) data;
        mCommentView.setCommentCount(commentOverall.getTotalCount());
        mTotalCount = commentOverall.getTotalCount();
        return super.setData(commentOverall.getResult(), index);
    }

    @Override
    public void toDetailPage(Comment data) {
        String userName = data.getName().trim();
        // 不可以回复我自己
        if (userName.equals("我")) {
            return;
        }
        mWhom = data.getWhom();
        mCommentView.setBottomReply(data.getName());
    }

    @Override
    public boolean onBackPressed() {
        if (!TextUtils.isEmpty(mWhom)) {
            mWhom = "";
            mCommentView.setBottomSend();
            return true;
        }
        return false;
    }

    public void onSubmitClicked(String comment) {
        submitComment(comment);
        mWhom = "";
        mCommentView.setBottomSend();
    }

    public abstract void submitComment(String comment);

    // 将评论数返回
    public int getCommentCount() {
        return mTotalCount;
    }
}
