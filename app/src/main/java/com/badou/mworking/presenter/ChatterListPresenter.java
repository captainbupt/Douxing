package com.badou.mworking.presenter;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.domain.ChatterListUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.util.SP;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ChatterListPresenter extends ListPresenter<Chatter> {

    ChatterListUseCase mChatterListUseCase;
    ChatterListFragment mFragment;
    String mTopic;

    public ChatterListPresenter(Context context, ChatterListFragment fragment, String topic) {
        super(context);
        this.mFragment = fragment;
        this.mTopic = topic;
    }

    public ChatterListPresenter(Context context, ChatterListFragment fragment) {
        this(context, fragment, null);
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
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mChatterListUseCase == null)
            mChatterListUseCase = new ChatterListUseCase(mTopic);
        mChatterListUseCase.setPageNum(pageIndex);
        return mChatterListUseCase;
    }

    @Override
    public void toDetailPage(Chatter data) {
        // 跳转到单条的Item的页面，并传递数据
        Intent intent = new Intent(mContext, ChatterDetailActivity.class);
        intent.putExtra(ChatterDetailActivity.KEY_CHATTER, data);
        mFragment.startActivityForResult(intent, REQUEST_DETAIL);
    }
}
