package com.badou.mworking.presenter.chatter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.domain.chatter.ChatterListUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.SP;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class ChatterListPresenter extends ListPresenter<Chatter> {

    ChatterListUseCase mChatterListUseCase;
    ChatterListFragment mFragment;
    String mTopic;
    String mUid;

    public ChatterListPresenter(Context context, ChatterListFragment fragment, String info, boolean isTopic) {
        super(context);
        this.mFragment = fragment;
        if (isTopic) {
            this.mTopic = info;
        } else {
            this.mUid = info;
        }
    }

    public ChatterListPresenter(Context context, ChatterListFragment fragment) {
        this(context, fragment, null, false);
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
    public void toDetailPage(Chatter data) {
        // 跳转到单条的Item的页面，并传递数据
        Intent intent = ChatterDetailActivity.getIntent(mContext, data.getQid());
        mFragment.startActivityForResult(intent, REQUEST_DETAIL);
    }
}
