package com.badou.mworking.presenter.chatter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.domain.chatter.ChatterListUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.chatter.ChatterPraiseUseCase;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.SP;
import com.google.gson.reflect.TypeToken;

import junit.framework.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class ChatterListPresenter extends ListPresenter<Chatter> {

    ChatterListUseCase mChatterListUseCase;
    ChatterListFragment mFragment;
    String mTopic;
    String mUid;
    String mHead;
    int mLevel;

    public ChatterListPresenter(Context context, ChatterListFragment fragment, String topic) {
        super(context);
        this.mFragment = fragment;
        this.mTopic = topic;
    }

    public ChatterListPresenter(Context context, ChatterListFragment fragment, String uid, String head, int level) {
        super(context);
        mFragment = fragment;
        mUid = uid;
        mHead = head;
        mLevel = level;
    }

    public ChatterListPresenter(Context context, ChatterListFragment fragment) {
        super(context);
        this.mFragment = fragment;
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Chatter>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return SP.CHATTER;
    }

    @Override
    public void onResponseItem(int position, Serializable item) {
        mBaseListView.setItem(position, GsonUtil.fromJson((String) item, Chatter.class));
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mChatterListUseCase == null) {
            mChatterListUseCase = new ChatterListUseCase(mTopic);
            if (!TextUtils.isEmpty(mUid)) {
                mChatterListUseCase.setUid(mUid);
            }
        }
        mChatterListUseCase.setPageNum(pageIndex);
        return mChatterListUseCase;
    }

    @Override
    protected boolean setList(List<Chatter> data, int index) {
        if (!TextUtils.isEmpty(mUid)) { // 获取某一用户同事圈的时候，没有头像和等级，需要手动写入
            for (Chatter chatter : data) {
                chatter.setHeadUrl(mHead);
                chatter.setLevel(mLevel);
            }
        }
        return super.setList(data, index);
    }

    @Override
    public void toDetailPage(Chatter data) {
        // 跳转到单条的Item的页面，并传递数据
        Intent intent = ChatterDetailActivity.getIntent(mContext, data.getQid());
        mFragment.startActivityForResult(intent, REQUEST_DETAIL);
    }

    public void toUserList(Chatter chatter) {
        if (chatter.getName().equals("神秘的TA")) {
            return;
        }
        mContext.startActivity(ChatterUserActivity.getIntent(mContext, new UserChatterInfo(chatter)));
    }

    public void praise(final Chatter chatter, final int position) {
        mBaseListView.showProgressDialog(R.string.progress_tips_praise_ing);
        new ChatterPraiseUseCase(chatter.getQid()).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ChatterResManager.insertItem(mContext, chatter);
                chatter.increasePraise();
                mBaseListView.setItem(position, chatter);
            }

            @Override
            public void onCompleted() {
                mBaseListView.hideProgressDialog();
            }
        });
    }
}
