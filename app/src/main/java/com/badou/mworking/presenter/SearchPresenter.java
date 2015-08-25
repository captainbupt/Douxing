package com.badou.mworking.presenter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.badou.mworking.domain.SearchUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.CategorySearch;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.factory.CategoryIntentFactory;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.MainSearchView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchPresenter extends ListPresenter<CategorySearch> {

    Handler mHandler;
    SearchUseCase mUseCase;
    String mCurrentKey;
    MainSearchView mSearchView;

    public SearchPresenter(Context context) {
        super(context);
        mHandler = new Handler();
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mSearchView = (MainSearchView) v;
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<CategorySearch>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mUseCase == null) {
            mUseCase = new SearchUseCase();
        }
        mUseCase.setKey(mCurrentKey);
        return mUseCase;
    }

    @Override
    protected boolean setData(Object data, int index) {
        CategorySearchOverall overall = (CategorySearchOverall) data;
        List<CategorySearch> list = new ArrayList<>();
        list.addAll(overall.getNotice());
        list.addAll(overall.getTraining());
        list.addAll(overall.getExam());
        list.addAll(overall.getTask());
        list.addAll(overall.getShelf());
        list.addAll(overall.getEntry());
        list.addAll(overall.getPlan());
        list.addAll(overall.getSurvey());
        return super.setData(list, index);
    }

    @Override
    public void toDetailPage(CategorySearch data) {
        mContext.startActivity(CategoryIntentFactory.getIntent(mContext, data.type, data.rid));
    }

    @Override
    public boolean onBackPressed() {
        if (!TextUtils.isEmpty(mCurrentKey)) {
            mCurrentKey = "";
            mSearchView.clear();
            return true;
        }
        return super.onBackPressed();
    }

    public void onTextChange(String text) {
        mCurrentKey = text;
        if (TextUtils.isEmpty(text)) {
            mSearchView.clear();
            mHandler.removeCallbacks(mUpdateRunnable);
        } else {
            mSearchView.hideNoneResult();
            // 避免过快刷新
            mHandler.removeCallbacks(mUpdateRunnable);
            mHandler.postDelayed(mUpdateRunnable, 1000);
        }
    }

    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            mSearchView.startRefreshing();
            refresh();
        }
    };

}
