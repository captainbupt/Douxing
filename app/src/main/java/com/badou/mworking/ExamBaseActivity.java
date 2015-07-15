package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RequestParameters;
import com.easemob.chatuidemo.domain.User;

import butterknife.ButterKnife;
import butterknife.Bind;

public class ExamBaseActivity extends BaseBackActionBarActivity {

    public static final String KEY_EXAM = "exam";
    public static final String RESPONSE_EXAM = "exam";
    protected Exam mExam;
    @Bind(R.id.content_container)
    FrameLayout mContentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_exam);
        setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_EXAM).getName());
        ButterKnife.bind(this);
        mExam = (Exam) mReceivedIntent.getSerializableExtra(KEY_EXAM);
        addStoreImageView(mExam.isStore(), Store.TYPE_STRING_EXAM, mExam.getRid());
        if (UserInfo.getUserInfo().isAdmin()) {
            addStatisticalImageView(mExam.getRid());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESPONSE_EXAM, mExam);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onStoreChanged(boolean isStore) {
        mExam.setStore(isStore);
    }
}
