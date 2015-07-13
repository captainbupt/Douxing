package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrainBaseActivity extends BaseBackActionBarActivity {

    public static final String KEY_TRAINING = "training";
    public static final String RESPONSE_TRAINING = "training";
    protected Train mTrain;
    @InjectView(R.id.content_container)
    FrameLayout mContentContainer;
    @InjectView(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_training);
        ButterKnife.inject(this);
        mTrain = (Train) mReceivedIntent.getSerializableExtra(KEY_TRAINING);
        mTrain.setRead(true);
        mBottomView.setData(mTrain.getRid(), mTrain.getRatingNumber(), mTrain.getCommentNumber(), mTrain.getRating());
        mBottomView.updateData();
        if (mTrain.isTraining()) {
            setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_TRAINING).getName());
            addStoreImageView(mTrain.isStore(), Store.TYPE_STRING_TRAINING, mTrain.getRid());
        } else {
            setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_SHELF).getName());
            addStoreImageView(mTrain.isStore(), Store.TYPE_STRING_SHELF, mTrain.getRid());
        }
        if (UserInfo.getUserInfo().isAdmin()) {
            addStatisticalImageView(mTrain.getRid());
        }

        mBottomView.setOnRatingCommentDataUpdated(new BottomRatingAndCommentView.OnRatingCommentDataUpdated() {
            @Override
            public void onDataChanged(int ratingNumber, int commentNumber, int currentRating) {
                mTrain.setRatingValue(currentRating, ratingNumber);
                mTrain.setCommentNumber(commentNumber);
            }
        });
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
                mTrain.setCommentNumber(allCount);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESPONSE_TRAINING, mTrain);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onStoreChanged(boolean isStore) {
        mTrain.setStore(isStore);
    }
}
