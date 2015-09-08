package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.fragment.CommentFragment;
import com.badou.mworking.fragment.PlanIntroductionFragment;
import com.badou.mworking.fragment.PlanStageFragment;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.presenter.category.PlanPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.category.PlanView;
import com.badou.mworking.widget.CategoryHeader;
import com.badou.mworking.widget.CategoryTabContent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanActivity extends BaseNoTitleActivity implements PlanView {

    @Bind(R.id.header)
    CategoryHeader mHeader;
    @Bind(R.id.content)
    CategoryTabContent mContent;

    PlanPresenter mPresenter;
    ImageView mStoreImageView;
    ImageView mStatisticalImageView;
    ImageView mSettingImageView;

    public static Intent getIntent(Context context, String rid, String planTitle) {
        return CategoryBaseActivity.getIntent(context, PlanActivity.class, rid, planTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);
        initView();
        String rid = mReceivedIntent.getStringExtra(CategoryBaseActivity.KEY_RID);
        final PlanIntroductionFragment planIntroductionFragment = PlanIntroductionFragment.getFragment(rid);
        final PlanStageFragment operationFragment = PlanStageFragment.getFragment(rid);
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
        mStatisticalImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_statistical_round);
        mStatisticalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStatisticalClicked();
            }
        });
        mHeader.addRightImage(mStatisticalImageView);//添加右侧图标1

        mStoreImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_store_round_checked);
        mStoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStoreClicked();
            }
        });
        mHeader.addRightImage(mStoreImageView);//添加右侧图标2


        mSettingImageView = BaseActionBarActivity.getDefaultImageView(mContext, R.drawable.button_title_menu_round);

        mSettingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSettingClicked();//修改 CategoryBasePresenter中添加方法
            }
        });
        mHeader.addRightImage(mSettingImageView);//添加右侧图标3
        mHeader.setLeftClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Presenter getPresenter() {
        String rid = mReceivedIntent.getStringExtra(CategoryBaseActivity.KEY_RID);
        return new PlanPresenter(mContext, rid, mReceivedIntent.getStringExtra(CategoryBaseActivity.KEY_PLAN_TITLE));
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, boolean isPlan) {
        if (isPlan) {
            mStoreImageView.setVisibility(View.GONE);
        } else {
            setStore(categoryDetail.isStore());
        }
        setStore(categoryDetail.isStore());
        mHeader.setTitle(categoryDetail.getSubject());
        setStageTitle(categoryDetail.getPlan().getCurrentStage().getSubject());
        mHeader.setBackgroundImageView(categoryDetail.getImg());
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
    public void hideCommentView() {

    }

    @Override
    public void setStore(boolean isStore) {
        mStoreImageView.setImageResource(isStore ? R.drawable.button_title_store_round_checked : R.drawable.button_title_store_round_unchecked);
    }

    @Override
    public void setStageTitle(String stageTitle) {
        mHeader.setSubTitle("| " + stageTitle + " |");
    }

    @Override
    public void setActionbarTitle(String title) {
        mHeader.setTitle(title);
    }
}


