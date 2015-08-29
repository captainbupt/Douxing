package com.badou.mworking.presenter.ask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskAnswerSubmitActivity;
import com.badou.mworking.MultiPhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.AskResManager;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.ask.AskDeleteUseCase;
import com.badou.mworking.domain.ask.AskReplyGetUseCase;
import com.badou.mworking.domain.ask.AskReplyPraiseUseCase;
import com.badou.mworking.domain.ask.AskUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ask.AskDetailView;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AskDetailPresenter extends ListPresenter<Ask> {

    private final static int REQUEST_REPLY = 213;

    String mAid;
    Ask mAsk;
    AskDetailView mDetailView;
    AskReplyGetUseCase mAskReplyGetUseCase;
    AskReplyPraiseUseCase mAskReplyPraiseUseCase;
    StoreUseCase mStoreUseCase;

    public AskDetailPresenter(Context context, String aid) {
        super(context);
        this.mAid = aid;
    }

    @Override
    public void attachView(BaseView v) {
        mDetailView = (AskDetailView) v;
        super.attachView(v);
    }

    protected void initialize() {
        mDetailView.showProgressDialog();
        new AskUseCase(mAid).execute(new BaseSubscriber<Ask>(mContext) {
            @Override
            public void onResponseSuccess(Ask data) {
                mAsk = data;
                mDetailView.setData(data);
                // 先获取详情，再刷新列表
                AskDetailPresenter.super.initialize();
            }

            @Override
            public void onCompleted() {
                mDetailView.hideProgressDialog();
            }
        });
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Ask>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mAskReplyGetUseCase == null) {
            mAskReplyGetUseCase = new AskReplyGetUseCase(mAid);
        }
        mAskReplyGetUseCase.setPageNum(pageIndex);
        return mAskReplyGetUseCase;
    }

    @Override
    public void toDetailPage(Ask data) {
        DialogUtil.showCopyDialog(mContext, data.getContent());
    }

    public void showAskFullImage() {
        showFullImage(mAsk.getContentImageUrl());
    }

    public void showFullImage(final String url) {
        mContext.startActivity(MultiPhotoActivity.getIntentFromWeb(mContext, new ArrayList<String>() {{
            add(url);
        }}, 0));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_REPLY) {
            mAsk.increaseCount();
            mDetailView.setReplyCount(mAsk.getCount());
            mDetailView.startRefreshing();
        }
    }

    public void deleteAsk() {
        DialogUtil.showDeleteDialog(mContext, new DialogUtil.OnConfirmListener() {
            @Override
            public void onConfirm() {
                mDetailView.showProgressDialog(R.string.progress_tips_delete_ing);
                new AskDeleteUseCase(mAid).execute(new BaseSubscriber(mContext) {
                    @Override
                    public void onResponseSuccess(Object data) {
                        mAsk = null;
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

    public void submitReply() {
        ((Activity) mContext).startActivityForResult(AskAnswerSubmitActivity.getIntent(mContext, mAid), REQUEST_REPLY);
    }

    public Intent getResult() {
        if (mAsk == null) {
            return ListPresenter.getResultIntent(null, true);
        } else {
            return ListPresenter.getResultIntent(mAsk);
        }
    }

    public void copy(Ask ask) {
        DialogUtil.showCopyDialog(mContext, ask.getContent());
    }

    public void submitReply(Ask ask) {
        if (!ask.getUid().equals(UserInfo.getUserInfo().getUid()))
            ((Activity) mContext).startActivityForResult(AskAnswerSubmitActivity.getIntent(mContext, mAid, ask.getUid(), ask.getUserName()), REQUEST_REPLY);
    }

    public void praise(final Ask ask, final int position) {
        if (mAskReplyPraiseUseCase == null) {
            mAskReplyPraiseUseCase = new AskReplyPraiseUseCase(mAid);
        }
        mAskReplyPraiseUseCase.setTs(ask.getCreateTime());
        mAskReplyPraiseUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ask.increaseCount();
                AskResManager.insertItem(mAid, ask.getCreateTime());
                mDetailView.setItem(position, ask);
            }
        });
    }

    public void toMessage() {
        mContext.startActivity(ChatActivity.getSingleIntent(mContext, mAsk.getWhom()));
    }

    public void onStoreClicked() {
        if (mStoreUseCase == null)
            mStoreUseCase = new StoreUseCase(mAid, Store.TYPE_STRING_ASK);
        mStoreUseCase.onStoreClicked(mContext, mDetailView, mAsk);
    }
}
