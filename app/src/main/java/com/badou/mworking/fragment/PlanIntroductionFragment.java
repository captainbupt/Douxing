package com.badou.mworking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.category.PlanIntroductionPresenter;
import com.badou.mworking.view.category.PlanIntroductionView;
import com.badou.mworking.widget.CategoryTabContent;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanIntroductionFragment extends BaseFragment implements PlanIntroductionView, CategoryTabContent.ScrollableContent {

    private static final String KEY_RID = "rid";

    public static PlanIntroductionFragment getFragment(String rid) {
        PlanIntroductionFragment fragment = new PlanIntroductionFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        fragment.setArguments(argument);
        return fragment;
    }

    ScrollView mParentScrollView;


    @Bind(R.id.introduction_text_view)
    TextView mIntroductionTextView;

    PlanIntroductionPresenter mPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        mParentScrollView = (ScrollView) inflater.inflate(R.layout.fragment_plan_introduction, container, false);
        ButterKnife.bind(this, mParentScrollView);
        Bundle argument = getArguments();
        mPresenter = new PlanIntroductionPresenter(mContext, argument.getString(KEY_RID));
        mPresenter.attachView(this);
        return mParentScrollView;
    }

    public PlanIntroductionPresenter getPresenter() {
        return mPresenter;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 学习计划简介
     *
     * @param categoryDetail
     */
    @Override
    public void setData(CategoryDetail categoryDetail, int stageIndex) {
        mIntroductionTextView.setText(categoryDetail.getPlan().getStage(stageIndex).getDescription());
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, mParentScrollView, header);
    }

    @Override
    public String getTitle() {
        return getString(R.string.plan_introduction);
    }

    @Override
    public void onChange(float ratio, float offsetY) {
    }

    @Override
    public void onOffsetCalculated(int offset) {
    }
}