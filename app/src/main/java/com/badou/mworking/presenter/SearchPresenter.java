package com.badou.mworking.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.domain.SearchUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategorySearch;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.MainSearchView;
import com.badou.mworking.widget.WaitProgressDialog;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

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
        return super.setData(list, index);
    }

    @Override
    public void toDetailPage(CategorySearch data) {

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
