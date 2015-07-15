package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.Store;
import com.badou.mworking.model.category.Train;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TrainBaseActivity extends BaseBackActionBarActivity {

    public static final String KEY_TRAINING = "training";
    public static final String RESPONSE_TRAINING = "training";
    protected Train mTrain;
    @Bind(R.id.content_container)
    FrameLayout mContentContainer;
    @Bind(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_training);
        ButterKnife.bind(this);
        mTrain = (Train) mReceivedIntent.getSerializableExtra(KEY_TRAINING);
        mTrain.isRead = true;
        mBottomView.setData(mTrain.rid, mTrain.ecnt, mTrain.commentNum, mTrain.eval);
        mBottomView.updateData();
        if (mTrain.isTraining) {
            setActionbarTitle(MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_TRAINING).name);
            addStoreImageView(mTrain.isStore, Store.TYPE_STRING_TRAINING, mTrain.rid);
        } else {
            setActionbarTitle(MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_SHELF).name);
            addStoreImageView(mTrain.isStore, Store.TYPE_STRING_SHELF, mTrain.rid);
        }
        if (((AppApplication) getApplication()).getUserInfo().isAdmin) {
            addStatisticalImageView(mTrain.rid);
        }

        mBottomView.setOnRatingCommentDataUpdated(new BottomRatingAndCommentView.OnRatingCommentDataUpdated() {
            @Override
            public void onDataChanged(int ratingNumber, int commentNumber, int currentRating) {
                mTrain.rating = currentRating;
                mTrain.ecnt = ratingNumber;
                mTrain.commentNum = commentNumber;
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
                mTrain.commentNum = allCount;
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
        mTrain.isStore = isStore;
    }
}
