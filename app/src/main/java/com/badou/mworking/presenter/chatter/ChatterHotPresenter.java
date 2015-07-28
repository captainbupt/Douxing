package com.badou.mworking.presenter.chatter;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.domain.chatter.ChatterHotListUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.chatter.ChatterHot;
import com.badou.mworking.entity.chatter.ChatterHotOverall;
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
    public void toDetailPage(ChatterHot data) {
        Intent intent = new Intent(mContext, ChatterUserActivity.class);
        intent.putExtra(ChatterUserActivity.KEY_UID, data.getUid());
        mContext.startActivity(intent);
    }
}
