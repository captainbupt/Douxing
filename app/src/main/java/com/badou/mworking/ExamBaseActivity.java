package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.Store;
import com.badou.mworking.model.category.Exam;
import com.badou.mworking.model.category.Notice;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.widget.BottomRatingAndCommentView;

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
        setActionbarTitle(MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_EXAM).name);
        ButterKnife.bind(this);
        mExam = (Exam) mReceivedIntent.getSerializableExtra(KEY_EXAM);
        addStoreImageView(mExam.isStore, Store.TYPE_STRING_EXAM, mExam.rid);
        if (((AppApplication) getApplication()).getUserInfo().isAdmin) {
            addStatisticalImageView(mExam.rid);
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
        mExam.isStore = isStore;
    }
}
