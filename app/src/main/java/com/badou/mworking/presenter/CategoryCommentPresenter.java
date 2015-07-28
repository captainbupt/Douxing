package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.domain.CategoryCommentGetUseCase;
import com.badou.mworking.domain.CategoryCommentSendUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CategoryCommentPresenter extends CommentPresenter {

    CategoryCommentGetUseCase mCommentGetUseCase;
    CategoryCommentSendUseCase mCommentSendUseCase;

    String mRid;

    public CategoryCommentPresenter(Context context, String rid) {
        super(context);
        mRid = rid;
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<CategoryComment>>() {
        }.getType();
    }

    @Override
    protected UseCase getRefreshUseCase(int pageNum) {
        if (mCommentGetUseCase == null)
            mCommentGetUseCase = new CategoryCommentGetUseCase(mRid);
        mCommentGetUseCase.setPageNum(pageNum);
        return mCommentGetUseCase;
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
}
