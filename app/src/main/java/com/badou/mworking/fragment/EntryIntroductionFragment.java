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
import com.badou.mworking.presenter.EntryIntroductionPresenter;
import com.badou.mworking.view.EntryIntroductionView;
import com.badou.mworking.widget.CategoryTabContent;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EntryIntroductionFragment extends BaseFragment implements EntryIntroductionView, CategoryTabContent.ScrollableContent {


    public static final String KEY_RID = "rid";

    public static EntryIntroductionFragment getFragment(String rid) {
        EntryIntroductionFragment fragment = new EntryIntroductionFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        fragment.setArguments(argument);
        return fragment;
    }

    ScrollView mParentScrollView;
    @Bind(R.id.entry_begin_text_view)
    TextView mEntryBeginTextView;
    @Bind(R.id.activity_begin_text_view)
    TextView mActivityBeginTextView;
    @Bind(R.id.number_text_view)
    TextView mNumberTextView;
    @Bind(R.id.introduction_text_view)
    TextView mIntroductionTextView;
    @Bind(R.id.status_text_view)
    TextView mStatusTextView;
    @Bind(R.id.entry_text_view)
    TextView mEntryTextView;

    EntryIntroductionPresenter mPresenter;

    public EntryIntroductionFragment() {
        mPresenter = new EntryIntroductionPresenter(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentScrollView = (ScrollView) inflater.inflate(R.layout.fragment_entry_introduction, container, false);
        ButterKnife.bind(this, mParentScrollView);
        Bundle argument = getArguments();
        mPresenter.setRid(argument.getString(KEY_RID));
        mPresenter.attachView(this);
        return mParentScrollView;
    }

    public EntryIntroductionPresenter getPresenter() {
        return mPresenter;
    }

    @OnClick(R.id.entry_text_view)
    void onSignClicked() {
        mPresenter.onSignClicked();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setData(CategoryDetail categoryDetail) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        mEntryBeginTextView.append(df.format(new Date(categoryDetail.getEntry().getStartline())) + " 至" + df.format(new Date(categoryDetail.getEntry().getDeadline())));
        mActivityBeginTextView.append(df.format(new Date(categoryDetail.getEntry().getStartline_c())) + " 至" + df.format(new Date(categoryDetail.getEntry().getDeadline_c())));
        mNumberTextView.setText(categoryDetail.getEntry().getEnroll() + "/" + categoryDetail.getEntry().getMaxusr());
        mIntroductionTextView.setText(categoryDetail.getEntry().getContent().getDescription());
        if (categoryDetail.getEntry().isStarted()) {
            if (!categoryDetail.getEntry().isOffline()) {
                switch (categoryDetail.getEntry().getIn()) {
                    case 0:
                        setStatusText(R.string.entry_action_enroll, true, -1);
                        break;
                    case 1:
                        setStatusText(R.string.entry_action_enroll_cancel, true, R.string.entry_status_check_ing);
                        break;
                    case 2:
                        setStatusText(R.string.entry_action_enroll_success, false, -1);
                        break;
                    case 3:
                        setStatusText(R.string.entry_action_enroll_fail, false, -1);
                        break;
                }
            } else {
                switch (categoryDetail.getEntry().getIn()) {
                    case 0:
                        setStatusText(R.string.entry_action_enroll_expired, false, -1);
                        break;
                    case 1:
                        setStatusText(R.string.entry_action_enroll_expired, false, R.string.entry_status_check_fail);
                        break;
                    case 2:
                        setStatusText(R.string.entry_action_enroll_success, false, -1);
                        break;
                    case 3:
                        setStatusText(R.string.entry_action_enroll_fail, false, -1);
                        break;
                }
            }
        } else {
            setStatusText(R.string.entry_action_not_started, false, -1);
        }
    }

    @Override
    public void setStatusText(int buttonResId, boolean isEnable, int statusResId) {
        mEntryTextView.setText(buttonResId);
        if (isEnable) {
            mEntryTextView.setTextColor(getResources().getColorStateList(R.color.color_button_text_blue));
            mEntryTextView.setBackgroundResource(R.drawable.background_button_enable_blue);
        } else {
            mEntryTextView.setTextColor(getResources().getColor(R.color.color_text_black));
            mEntryTextView.setBackgroundResource(R.drawable.background_button_disable);
        }
        if (statusResId > 0) {
            mStatusTextView.setText(statusResId);
            mStatusTextView.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, mParentScrollView, header);
    }

    @Override
    public String getTitle() {
        return getString(R.string.entry_introduction);
    }

    @Override
    public void onChange(float ratio, float offsetY) {
    }

    @Override
    public void onOffsetCalculated(int offset) {
    }
}
