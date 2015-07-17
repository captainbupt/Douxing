package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.CategoryBasePresenter;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrainBaseActivity extends CategoryBaseActivity {

    public static final String KEY_TRAINING = "training";

    @Bind(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;
    @Bind(R.id.content_container)
    FrameLayout mContentContainer;

    public static Intent getIntent(Context context, Class clz, String rid, boolean isTraining) {
        Intent intent = CategoryBaseActivity.getIntent(context, clz, rid);
        intent.putExtra(KEY_TRAINING, isTraining);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_training);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        boolean isTraining = getIntent().getBooleanExtra(KEY_TRAINING, true);
        if (isTraining) {
            setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_TRAINING));
        } else {
            setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_SHELF));
        }
        mBottomView.setCommentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onCommentClicked();
            }
        });
        mBottomView.setRatingClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onRatingClicked();
            }
        });
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        boolean isTraining = mReceivedIntent.getBooleanExtra(KEY_TRAINING, true);
        return new CategoryBasePresenter(mContext, isTraining ? Category.CATEGORY_TRAINING : Category.CATEGORY_SHELF, mReceivedIntent.getStringExtra(KEY_RID));
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, mContentContainer, false);
        mContentContainer.addView(view);
    }

    @Override
    public void setCommentNumber(int number) {
        mBottomView.setCommentData(number);
    }

    @Override
    public void setRatingNumber(int number) {
        mBottomView.setRatingData(number);
    }

}
