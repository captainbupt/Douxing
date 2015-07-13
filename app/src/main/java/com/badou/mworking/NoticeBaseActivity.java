package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoticeBaseActivity extends BaseBackActionBarActivity {

    public static final String KEY_NOTICE = "training";
    public static final String RESPONSE_NOTICE = "training";
    protected Notice mNotice;
    @InjectView(R.id.content_container)
    FrameLayout mContentContainer;
    @InjectView(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_notice);
        setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_NOTICE).getName());
        ButterKnife.inject(this);
        mNotice = (Notice) mReceivedIntent.getSerializableExtra(KEY_NOTICE);
        mNotice.setRead(true);
        mBottomView.setData(mNotice.getRid(), 0, mNotice.getCommentNumber(), 0);

        mBottomView.setOnRatingCommentDataUpdated(new BottomRatingAndCommentView.OnRatingCommentDataUpdated() {
            @Override
            public void onDataChanged(int ratingNumber, int commentNumber, int currentRating) {
                mNotice.setCommentNumber(commentNumber);
            }
        });

        addStoreImageView(mNotice.isStore(), Store.TYPE_STRING_NOTICE, mNotice.getRid());
        if (UserInfo.getUserInfo().isAdmin()) {
            addStatisticalImageView(mNotice.getRid());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == BottomRatingAndCommentView.REQUEST_COMMENT && data != null) {
            int allCount = data.getIntExtra(CommentActivity.RESPONSE_COUNT, -1);
            if (allCount >= 0) {
                mBottomView.setCommentData(allCount);
                mNotice.setCommentNumber(allCount);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESPONSE_NOTICE, mNotice);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onStoreChanged(boolean isStore) {
        mNotice.setStore(isStore);
    }
}
