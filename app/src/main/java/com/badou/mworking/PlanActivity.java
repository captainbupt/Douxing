package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.fragment.CommentFragment;
import com.badou.mworking.fragment.PlanIntroductionFragment;
import com.badou.mworking.fragment.PlanOperationFragment;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.presenter.PlanPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.PlanView;
import com.badou.mworking.widget.CategoryHeader;
import com.badou.mworking.widget.CategoryTabContent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by badou1 on 2015/7/29.
 */
public class PlanActivity extends BaseNoTitleActivity implements PlanView {

        @Bind(R.id.header)
        CategoryHeader mHeader;
        @Bind(R.id.content)
        CategoryTabContent mContent;

        PlanPresenter mPresenter;
        ImageView mStoreImageView;
        ImageView statisticalImageView;
        ImageView mCaidanImageView;

    public static Intent getIntent(Context context, String rid) {
        return CategoryBaseActivity.getIntent(context, PlanActivity.class, rid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);
        initView();
        String rid = mReceivedIntent.getStringExtra(CategoryBaseActivity.KEY_RID);
        final PlanIntroductionFragment planIntroductionFragment = PlanIntroductionFragment.getFragment(rid);
        final PlanOperationFragment operationFragment = PlanOperationFragment.getFragment(rid);
        final CommentFragment commentFragment = CommentFragment.getFragment(rid);
        commentFragment.setOnCommentCountChangedListener(new CommentFragment.OnCommentCountChangedListener() {
            @Override
            public void onCommentCountChanged(int count) {
                mContent.notifyDataSetChanged();
            }
        });

        List<CategoryTabContent.ScrollableContent> list = new ArrayList<>();
        list.add(planIntroductionFragment);
        list.add(operationFragment);
        list.add(commentFragment);
        mContent.setList(list);
        mContent.setSwipeEnabled(true);//hua
        mPresenter = (PlanPresenter) super.mPresenter;
        mContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.setChildPresenters(planIntroductionFragment.getPresenter(), operationFragment.getPresenter(), commentFragment.getPresenter());
                mPresenter.attachView(PlanActivity.this);
            }
        }, 200);

    }

    public void initView() {
        statisticalImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_statistical_round);
        statisticalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStatisticalClicked();
            }
        });
        mHeader.addRightImage(statisticalImageView);//添加右侧图标1

        mStoreImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_store_round_checked);
        mStoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStoreClicked();
            }
        });
        mHeader.addRightImage(mStoreImageView);//添加右侧图标2


        mCaidanImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_menu_round );

        mCaidanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onPlanDatile();//修改 CategoryBasePresenter中添加方法
             }
        });
        mHeader.addRightImage(mCaidanImageView);//添加右侧图标3

        mHeader.setLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public Presenter getPresenter() {
        String rid = mReceivedIntent.getStringExtra(CategoryBaseActivity.KEY_RID);
        return new PlanPresenter(mContext, rid);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        setStore(categoryDetail.isStore());
        mHeader.setTitle(categoryDetail.getSubject());

    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.onBackPressed())
            super.onBackPressed();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, ListPresenter.getResultIntent(mPresenter.getData()));
        super.finish();
    }

    @Override
    public void setCommentNumber(int number) {

    }

    @Override
    public void setRatingNumber(int number) {

    }

    @Override
    public void setStore(boolean isStore) {
        mStoreImageView.setImageResource(isStore ? R.drawable.button_title_store_round_checked : R.drawable.button_title_store_round_unchecked);
    }

    @Override
    public void setSwipeEnable(boolean isEnable) {
        mContent.setSwipeEnabled(isEnable);
    }
}


