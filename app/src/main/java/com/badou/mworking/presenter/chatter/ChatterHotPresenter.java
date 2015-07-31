package com.badou.mworking.presenter.chatter;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.domain.UserDetailUseCase;
import com.badou.mworking.domain.chatter.ChatterHotListUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.chatter.ChatterHot;
import com.badou.mworking.entity.chatter.ChatterHotOverall;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.SP;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ChatterHotPresenter extends ListPresenter<ChatterHot> {

    ChatterHotListUseCase mChatterHotListUseCase;

    public ChatterHotPresenter(Context context) {
        super(context);
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<ChatterHot>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return SP.CHATTERHOT;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mChatterHotListUseCase == null)
            mChatterHotListUseCase = new ChatterHotListUseCase();
        mChatterHotListUseCase.setPageNum(pageIndex);
        return mChatterHotListUseCase;
    }

    @Override
    protected boolean setData(Object data, int index) {
        return setList(((ChatterHotOverall) data).getHotList(), index);
    }

    @Override
    public void toDetailPage(final ChatterHot data) {
        mBaseListView.showProgressDialog();
        new UserDetailUseCase(data.getUid()).execute(new BaseSubscriber<UserDetail>(mContext) {
            @Override
            public void onResponseSuccess(UserDetail userDetail) {
                Intent intent = ChatterUserActivity.getIntent(mContext, new UserChatterInfo(data.getUid(), userDetail));
                mContext.startActivity(intent);
            }

            @Override
            public void onCompleted() {
                mBaseListView.hideProgressDialog();
            }
        });
    }
}
