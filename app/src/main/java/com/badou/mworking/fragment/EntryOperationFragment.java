package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.EntryOperationAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.category.EntryOperation;
import com.badou.mworking.presenter.category.EntryOperationPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.category.EntryOperationView;
import com.badou.mworking.widget.CategoryTabContent;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class EntryOperationFragment extends BaseFragment implements EntryOperationView, CategoryTabContent.ScrollableContent {

    private static final String KEY_RID = "rid";

    public static EntryOperationFragment getFragment(String rid) {
        EntryOperationFragment fragment = new EntryOperationFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        fragment.setArguments(argument);
        return fragment;
    }

    @Bind(R.id.content_list_view)
    ListView mContentListView;

    EntryOperationPresenter mPresenter;
    EntryOperationAdapter mEntryOperationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_operation, container, false);
        ButterKnife.bind(this, view);
        initView();
        Bundle argument = getArguments();
        mPresenter = new EntryOperationPresenter(mContext, this, argument.getString(KEY_RID));
        mPresenter.attachView(this);
        return view;
    }

    private void initView() {
        LinearLayout header = new LinearLayout(mContext);
        header.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View divider = new View(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, 1));
        lp.setMargins(0, DensityUtil.getInstance().getOffsetLess(), 0, 0);
        divider.setLayoutParams(lp);
        divider.setBackgroundColor(getResources().getColor(R.color.color_border_grey));
        header.addView(divider);
        mContentListView.addHeaderView(header);
        mEntryOperationAdapter = new EntryOperationAdapter(mContext);
        mContentListView.setAdapter(mEntryOperationAdapter);
    }

    public EntryOperationPresenter getPresenter() {
        return mPresenter;
    }

    @OnItemClick(R.id.content_list_view)
    void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 1)
            mPresenter.onItemClick((EntryOperation) parent.getAdapter().getItem(position), position - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNoneResult() {

    }

    @Override
    public void hideNoneResult() {

    }

    @Override
    public void disablePullUp() {

    }

    @Override
    public void enablePullUp() {

    }

    @Override
    public void startRefreshing() {

    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public void refreshComplete() {

    }

    @Override
    public void setData(List data) {
        mEntryOperationAdapter.setList(data);
    }

    @Override
    public void addData(List data) {
        mEntryOperationAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mEntryOperationAdapter.getCount();
    }

    @Override
    public void setItem(int index, EntryOperation item) {
        mEntryOperationAdapter.setItem(index, item);
    }

    @Override
    public EntryOperation getItem(int index) {
        return mEntryOperationAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mEntryOperationAdapter.remove(index);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, mContentListView, header);
    }

    @Override
    public String getTitle() {
        return getString(R.string.entry_operation);
    }

    @Override
    public void onOffsetCalculated(int offset) {

    }

    @Override
    public void onChange(float ratio, float offsetY) {

    }
}
