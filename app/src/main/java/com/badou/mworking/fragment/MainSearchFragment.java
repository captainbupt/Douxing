package com.badou.mworking.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.MainGridActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.MainSearchAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategorySearch;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.SearchPresenter;
import com.badou.mworking.view.MainSearchView;
import com.badou.mworking.widget.NoneResultView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainSearchFragment extends BaseFragment implements MainSearchView {


    @Bind(R.id.title_edit_text)
    EditText mTitleEditText;
    @Bind(R.id.cancel_text_view)
    TextView mCancelTextView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    @Bind(R.id.content_list_view)
    StickyListHeadersListView mContentListView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    SearchPresenter mPresenter;
    MainSearchAdapter mResultAdapter;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mTitleEditText != null)
            if (hidden) {
                hideFocus();
            } else {
                setFocus();
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_search, container, false);
        ButterKnife.bind(this, view);
        mResultAdapter = new MainSearchAdapter(mContext);
        mContentListView.setAdapter(mResultAdapter);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        initListener();
        mPresenter = new SearchPresenter(mContext);
        mPresenter.attachView(this);
        return view;
    }

    private void initListener() {
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onItemClick((CategorySearch) adapterView.getAdapter().getItem(i), i);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });
    }

    @OnClick(R.id.cancel_text_view)
    void onCancelClicked() {
        if (!mPresenter.onBackPressed()) {
            ((MainGridActivity) getActivity()).onBackPressed();
        }
    }

    @OnTextChanged(R.id.title_edit_text)
    void onTextChanged(Editable editable) {
        mPresenter.onTextChange(editable.toString());
    }

    @OnTouch(R.id.content_list_view)
    boolean onListTouch() {
        hideFocus();
        return false;
    }

    public void setFocus() {
        if (mTitleEditText == null)
            return;
        mTitleEditText.setFocusable(true);
        mTitleEditText.setFocusableInTouchMode(true);
        mTitleEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) mTitleEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mTitleEditText, 0);
    }

    public void hideFocus() {
        if (mTitleEditText == null)
            return;
        mTitleEditText.setFocusable(false);
        mTitleEditText.setFocusableInTouchMode(false);
        InputMethodManager inputManager = (InputMethodManager) mTitleEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mTitleEditText.getWindowToken(), 0);
    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void removeItem(int index) {
        mResultAdapter.remove(index);
    }

    @Override
    public void setItem(int index, CategorySearch item) {
        mResultAdapter.setItem(index, item);
    }

    @Override
    public int getDataCount() {
        return mResultAdapter.getCount();
    }

    @Override
    public void addData(List<CategorySearch> data) {
        mResultAdapter.addList(data);
    }

    @Override
    public void setData(List<CategorySearch> data) {
        mResultAdapter.setList(data);
    }

    @Override
    public void refreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void startRefreshing() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void enablePullUp() {
    }

    @Override
    public void disablePullUp() {
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.GONE);
    }

    @Override
    public void showNoneResult() {
        String key = mTitleEditText.getText().toString();
        String tip = getResources().getString(R.string.main_search_result_none);
        mNoneResultView.setContent(R.drawable.icon_main_search_tip, tip.replace("***", key));
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clear() {
        if (!TextUtils.isEmpty(mTitleEditText.getText().toString()))
            mTitleEditText.setText("");
        mResultAdapter.clear();
        mNoneResultView.setContent(R.drawable.icon_main_search_tip, R.string.main_search_result_tip);
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    public SearchPresenter getPresenter() {
        return mPresenter;
    }
}

