package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.presenter.CategoryBasePresenter;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoticeBaseActivity extends CategoryBaseActivity {

    @Bind(R.id.content_container)
    FrameLayout mContentContainer;
    @Bind(R.id.bottom_view)
    BottomRatingAndCommentView mBottomView;

    CategoryBasePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_notice);
        ButterKnife.bind(this);
        setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_NOTICE));
        initListener();
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        mPresenter = new CategoryBasePresenter(mContext, Category.CATEGORY_NOTICE);
        mPresenter.attachView(this);
        return mPresenter;
    }

    private void initListener() {
        mBottomView.setCommentClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onCommentClicked();
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
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setCommentNumber(int number) {
        mBottomView.setCommentData(number);
    }

    @Override
    public void setRatingNumber(int number) {

    }
}
