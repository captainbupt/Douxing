package com.badou.mworking.presenter.chatter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.R;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.chatter.ChatterDeleteUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyDeleteUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyGetUseCase;
import com.badou.mworking.domain.chatter.ChatterReplySendUseCase;
import com.badou.mworking.domain.chatter.ChatterUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.StoreItem;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.CommentPresenter;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.StoreItemView;
import com.badou.mworking.view.chatter.ChatterDetailView;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ChatterDetailPresenter extends CommentPresenter {

    String mQid;
    Chatter mChatter;
    ChatterReplyGetUseCase mReplyGetUseCase;
    ChatterReplySendUseCase mReplySendUseCase;
    ChatterDetailView mDetailView;
    StoreUseCase mStoreUseCase;

    public ChatterDetailPresenter(Context context, String qid) {
        super(context);
        this.mQid = qid;
    }

    @Override
    public void attachView(BaseView v) {
        mDetailView = (ChatterDetailView) v;
        super.attachView(v);
    }

    @Override
    protected void initialize() {
        mDetailView.showProgressDialog();
        new ChatterUseCase(mQid).execute(new BaseSubscriber<Chatter>(mContext) {
            @Override
            public void onResponseSuccess(Chatter data) {
                mChatter = data;
                mDetailView.setData(data);
                // 先获取详情，再刷新列表
                ChatterDetailPresenter.super.initialize();
            }

            @Override
            public void onCompleted() {
                mDetailView.hideProgressDialog();
            }
        });
    }

    @Override
    public void submitComment(String comment) {
        mDetailView.showProgressDialog(R.string.action_comment_update_ing);
        if (mReplySendUseCase == null)
            mReplySendUseCase = new ChatterReplySendUseCase(mQid);
        mReplySendUseCase.setData(comment, mWhom);
        mReplySendUseCase.execute(new BaseSubscriber<BaseNetEntity>(mContext) {
            @Override
            public void onResponseSuccess(BaseNetEntity data) {
                mDetailView.startRefreshing();
            }

            @Override
            public void onCompleted() {
                mDetailView.hideProgressDialog();
            }
        });
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<ChatterComment>>() {
        }.getType();
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mReplyGetUseCase == null)
            mReplyGetUseCase = new ChatterReplyGetUseCase(mQid);
        mReplyGetUseCase.setPageNum(pageIndex);
        return mReplyGetUseCase;
    }

    public Intent getResult() {
        if (mChatter == null) {
            return ListPresenter.getResultIntent(null, true);
        } else {
            mChatter.setReplyNumber(mDetailView.getAllCount());
            return ListPresenter.getResultIntent(GsonUtil.toJson(mChatter, Chatter.class));
        }
    }

    public void deleteChatter() {
        DialogUtil.showDeleteDialog(mContext, new DialogUtil.OnConfirmListener() {
            @Override
            public void onConfirm() {
                mDetailView.showProgressDialog(R.string.progress_tips_delete_ing);
                new ChatterDeleteUseCase(mQid).execute(new BaseSubscriber(mContext) {
                    @Override
                    public void onResponseSuccess(Object data) {
                        mChatter = null;
                        ((Activity) mContext).finish();
                    }

                    @Override
                    public void onCompleted() {
                        mDetailView.hideProgressDialog();
                    }
                });
            }
        });
    }

    public void toMessage() {
        mContext.startActivity(ChatActivity.getSingleIntent(mContext, mChatter.getWhom()));
    }

    public void deleteChatterReply(final int position) {
        DialogUtil.showDeleteDialog(mContext, new DialogUtil.OnConfirmListener() {
            @Override
            public void onConfirm() {
                mDetailView.showProgressDialog(R.string.progress_tips_delete_ing);
                new ChatterReplyDeleteUseCase(mQid, mDetailView.getDataCount() - position).execute(new BaseSubscriber(mContext) {
                    @Override
                    public void onResponseSuccess(Object data) {
                        mDetailView.showToast(R.string.chatter_tip_delete_success);
                        mDetailView.setCommentCount(mDetailView.getDataCount() - 1);
                        mDetailView.removeItem(position);
                    }

                    @Override
                    public void onCompleted() {
                        mDetailView.hideProgressDialog();
                    }
                });
            }
        });
    }

    public void onStoreClicked() {
        if (mStoreUseCase == null)
            mStoreUseCase = new StoreUseCase(mQid, Store.TYPE_STRING_CHATTER);
        mStoreUseCase.onStoreClicked(mContext, mDetailView, mChatter);
    }
}
