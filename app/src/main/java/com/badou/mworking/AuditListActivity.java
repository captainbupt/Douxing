package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.badou.mworking.adapter.AuditExpandableAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Audit;
import com.badou.mworking.presenter.AuditListPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.AuditListView;
import com.badou.mworking.widget.NoneResultView;
import com.idunnololz.widgets.AnimatedExpandableListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class AuditListActivity extends BaseBackActionBarActivity implements AuditListView {

    @Bind(R.id.content_list_view)
    AnimatedExpandableListView mContentListView;
    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPullToRefreshLayout;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    AuditExpandableAdapter mAuditExpandableAdapter;

    PopupWindow mAddPopupWindow;
    AuditListPresenter mPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, AuditListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_list);
        setActionbarTitle(R.string.audit_title);
        ButterKnife.bind(this);
        initialize();
        mPresenter = (AuditListPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    private void initialize() {
        View padding = new View(mContext);
        padding.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getInstance().getOffsetLless()));
        mContentListView.addHeaderView(padding);
        mContentListView.setGroupIndicator(null);

        mAuditExpandableAdapter = new AuditExpandableAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setAudit((Integer) v.getTag(), true);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setAudit((Integer) v.getTag(), false);
            }
        });
        mContentListView.setAdapter(mAuditExpandableAdapter);
        mContentListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mContentListView.isGroupExpanded(groupPosition)) {
                    mContentListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mContentListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
        mPullToRefreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.refresh();
            }
        });
        setRightImage(R.drawable.button_title_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
    }

    private void showPopupWindow() {
        if (mAddPopupWindow == null) {
            TextView textView = new TextView(mContext);
            textView.setBackgroundColor(0xff676568);
            textView.setTextColor(0xffffffff);
            textView.setText(R.string.audit_add);
            int padding = DensityUtil.getInstance().getOffsetMedium();
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.getInstance().getTextSizeMedium());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.addAudit();
                    mAddPopupWindow.dismiss();
                }
            });
            mAddPopupWindow = new PopupWindow(mContext);
            mAddPopupWindow.setContentView(textView);
            mAddPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mAddPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mAddPopupWindow.setFocusable(true);
            mAddPopupWindow.setOutsideTouchable(true);
            mAddPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        }
        mAddPopupWindow.showAsDropDown(mTitleRightContainer.getChildAt(0));
    }

    @Override
    public Presenter getPresenter() {
        return new AuditListPresenter(mContext);
    }

    @Override
    public void showNoneResult() {
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disablePullUp() {

    }

    @Override
    public void enablePullUp() {

    }

    @Override
    public void startRefreshing() {
        showProgressBar();
        mPullToRefreshLayout.autoRefresh();
    }

    @Override
    public boolean isRefreshing() {
        return mPullToRefreshLayout.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mPullToRefreshLayout.refreshComplete();
        hideProgressBar();
    }

    @Override
    public void setData(List<Audit> data) {
        mAuditExpandableAdapter.setData(data);
    }

    @Override
    public void addData(List<Audit> data) {

    }

    @Override
    public int getDataCount() {
        return mAuditExpandableAdapter == null ? 0 : mAuditExpandableAdapter.getParentList().size();
    }

    @Override
    public void setItem(int index, Audit item) {

    }

    @Override
    public Audit getItem(int index) {
        return mAuditExpandableAdapter.getParentList().get(index);
    }

    @Override
    public void removeItem(int index) {
        mAuditExpandableAdapter.removeItem(index);
    }
}
