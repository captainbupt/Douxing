package com.badou.mworking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.PlanStageAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.PlanIndex;
import com.badou.mworking.presenter.category.PlanStagePresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.category.PlanStageView;
import com.badou.mworking.widget.CategoryTabContent;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class PlanStageFragment extends BaseFragment implements PlanStageView, CategoryTabContent.ScrollableContent {

    private static final String KEY_RID = "rid";

    public static PlanStageFragment getFragment(String rid) {
        PlanStageFragment fragment = new PlanStageFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        fragment.setArguments(argument);
        return fragment;
    }

    @Bind(R.id.content_list_view)
    ListView mContentListView;

    PlanStagePresenter mPresenter;
    PlanStageAdapter mPlanStageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_operation, container, false);
        ButterKnife.bind(this, view);
        initView();
        Bundle argument = getArguments();

        mPresenter = new PlanStagePresenter(mContext, this, argument.getString(KEY_RID));
        mPresenter.attachView(this);
        return view;
    }

    private void initView() {
        View header = new View(mContext);
        header.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getInstance().getOffsetLess()));
        mContentListView.addHeaderView(header);

        mPlanStageAdapter = new PlanStageAdapter(mContext);
        mContentListView.setAdapter(mPlanStageAdapter);
    }

    public PlanStagePresenter getPresenter() {
        return mPresenter;
    }

    @OnItemClick(R.id.content_list_view)
    void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 1)
            mPresenter.onItemClick((CategoryBase) parent.getAdapter().getItem(position), position - 1);
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
        mPlanStageAdapter.setList(data);
    }

    @Override
    public void addData(List data) {
        mPlanStageAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mPlanStageAdapter.getCount();
    }

    @Override
    public void setItem(int index, CategoryBase item) {
        mPlanStageAdapter.setItem(index, item);
    }

    @Override
    public CategoryBase getItem(int index) {
        return mPlanStageAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mPlanStageAdapter.remove(index);
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
        return getString(R.string.plan_operation);
    }

    @Override
    public void onOffsetCalculated(int offset) {

    }

    @Override
    public void onChange(float ratio, float offsetY) {

    }

    @Override
    public void setStageIndex(int index) {
        mPlanStageAdapter.setStageIndex(index);
    }

    @Override
    public void setCurrentIndex(PlanIndex planIndex) {
        mPlanStageAdapter.setCurrentIndex(planIndex);
    }
}
