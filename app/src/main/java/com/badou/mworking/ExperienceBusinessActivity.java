package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.user.Business;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.widget.CornerRadiusButton;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExperienceBusinessActivity extends BaseNoTitleActivity{

    @Bind(R.id.business_text_view)
    TextView mBusinessTextView;
    @Bind({R.id.all_text_view, R.id.vehicle_text_view, R.id.advertisement_text_view, R.id.o2o_text_view, R.id.consumption_text_view, R.id.economic_text_view})
    List<TextView> mChoiceTextViews;
    @Bind(R.id.back_text_view)
    CornerRadiusButton mBackTextView;
    @Bind(R.id.confirm_text_view)
    CornerRadiusButton mConfirmTextView;

    private Business mCurrentBusiness;

    public static Intent getIntent(Context context) {
        return new Intent(context, ExperienceBusinessActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_business);
        ButterKnife.bind(this);
        ButterKnife.apply(mChoiceTextViews, OnClick);
        mBackTextView.setDisableMode();
        mConfirmTextView.setDisableMode();
        mConfirmTextView.setEnabled(false);
    }

    final ButterKnife.Action<View> OnClick = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, final int index) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (index < 0 || index >= UserInfo.ANONYMOUS_BUSINESS.size())
                        return;
                    mCurrentBusiness = UserInfo.ANONYMOUS_BUSINESS.get(index);
                    mBusinessTextView.setText(mCurrentBusiness.getTitle());
                    mConfirmTextView.setEnableMode();
                    mConfirmTextView.setEnabled(true);
                }
            });
        }
    };

    @butterknife.OnClick(R.id.confirm_text_view)
    void onConfirm() {
        if (mCurrentBusiness == null) {
            showToast(R.string.experience_business_empty);
        } else {
            startActivity(ExperienceInformationActivity.getIntent(mContext, mCurrentBusiness.getAccount(), mCurrentBusiness.getPassword()));
        }
    }

    @Override
    @OnClick(R.id.back_text_view)
    public void onBackPressed() {
        super.onBackPressed();
    }
}
