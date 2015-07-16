package com.badou.mworking.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.domain.CategoryCommentGetUseCase;
import com.badou.mworking.domain.CategoryCommentSendUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.CommentView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CommentPresenter extends ListPresenter<Comment> {

    public static final String KEY_RID = "rid";

    CommentView mCommentView;
    CategoryCommentGetUseCase mCommentGetUseCase;
    CategoryCommentSendUseCase mCommentSendUseCase;

    String mRid;
    String mWhom = null;
    int mTotalCount = 0;


    public CommentPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachIncomingArgument(Bundle bundle) {
        mRid = bundle.getString(KEY_RID);
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mCommentView = (CommentView) v;
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Comment>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageNum) {
        if (mCommentGetUseCase == null)
            mCommentGetUseCase = new CategoryCommentGetUseCase(mRid);
        mCommentGetUseCase.setPageNum(pageNum);
        return mCommentGetUseCase;
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

    public void submitComment(String comment) {
        mCommentView.showProgressDialog(R.string.action_comment_update_ing);
        if (mCommentSendUseCase == null)
            mCommentSendUseCase = new CategoryCommentSendUseCase(mRid);
        mCommentSendUseCase.setData(comment, mWhom);
        mCommentSendUseCase.execute(new BaseSubscriber<BaseNetEntity>(mContext) {
            @Override
            public void onResponseSuccess(BaseNetEntity data) {
                mCommentView.startRefreshing();
            }

            @Override
            public void onCompleted() {
                mCommentView.hideProgressDialog();
            }
        });
    }

    // 将评论数返回
    public int getCommentCount() {
        return mTotalCount;
    }
}
