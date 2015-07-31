package com.badou.mworking.presenter.ask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.AskSubmitActivity;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.ask.AskListUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.util.SP;
import com.badou.mworking.view.BaseView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AskPresenter extends ListPresenter<Ask> {

    private final int REQUEST_PUBLISH = 6456;

    AskListUseCase mAskListUseCase;

    public AskPresenter(Context context) {
        super(context);
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Ask>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return SP.ASK;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mAskListUseCase == null)
            mAskListUseCase = new AskListUseCase();
        mAskListUseCase.setPageNum(pageIndex);
        return mAskListUseCase;
    }

    @Override
    public void toDetailPage(Ask data) {
        ((Activity) mContext).startActivityForResult(AskDetailActivity.getIntent(mContext, data.getAid()), REQUEST_DETAIL);
    }

    public void publishAsk() {
        Intent intent = new Intent(mContext, AskSubmitActivity.class);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_PUBLISH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PUBLISH && resultCode == Activity.RESULT_OK) {
            mBaseListView.startRefreshing();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void copy(Ask ask) {
        DialogUtil.showCopyDialog(mContext, ask.getSubject());
    }
}
