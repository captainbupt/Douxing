package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.adapter.CategoryBaseAdapter;
import com.badou.mworking.adapter.ClassificationAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.factory.CategoryAdapterFactory;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.category.CategoryListPresenter;
import com.badou.mworking.presenter.category.ExamListPresenter;
import com.badou.mworking.presenter.category.SurveyListPresenter;
import com.badou.mworking.presenter.category.TrainingListPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.category.CategoryListView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.NoneResultView;
import com.badou.mworking.widget.VerticalSpaceItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnTouch;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class CategoryListActivity extends BaseBackActionBarActivity implements CategoryListView {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_IS_DONE = "done";

    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    @Bind(R.id.classification_main_list)
    ListView mClassificationMainList;
    @Bind(R.id.classification_more_list)
    ListView mClassificationMoreList;
    @Bind(R.id.classification_container)
    LinearLayout mClassificationContainer;
    @Bind(R.id.classification_background)
    FrameLayout mClassificationBackground;
    @Bind(R.id.content_list_view)
    RecyclerView mContentListView;
    @Bind(R.id.ptr_classic_frame_layout)
    PtrClassicFrameLayout mPtrClassicFrameLayout;

    private ImageView mTitleTriangleImageView;
    private View mTitleLayout;
    private TextView mTitleReadTextView;

    private ClassificationAdapter mMainClassificationAdapter = null;
    private ClassificationAdapter mMoreClassificationAdapter = null;
    protected CategoryBaseAdapter mCategoryAdapter = null;
    private int mCategoryIndex;

    CategoryListPresenter mPresenter;

    // 正常情况下isDone为false
    public static Intent getIntent(Context context, int category, boolean isDone) {
        Intent intent = new Intent(context, CategoryListActivity.class);
        intent.putExtra(KEY_CATEGORY, category);
        intent.putExtra(KEY_IS_DONE, isDone);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_progress_list);
        ButterKnife.bind(this);
        initTitleView();
        initClassificationView();
        initListView();
        mPresenter = (CategoryListPresenter) super.mPresenter;
        mPresenter.setIsDone(mReceivedIntent.getBooleanExtra(KEY_IS_DONE, false));
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        mCategoryIndex = mReceivedIntent.getIntExtra(KEY_CATEGORY, -1);
        if (mCategoryIndex == -1) {
            finish();
        }
        switch (mCategoryIndex) {
            case Category.CATEGORY_TRAINING:
                return new TrainingListPresenter(mContext, mCategoryIndex);
            case Category.CATEGORY_SHELF:
                return new TrainingListPresenter(mContext, mCategoryIndex);
            case Category.CATEGORY_EXAM:
                return new ExamListPresenter(mContext, mCategoryIndex);
            case Category.CATEGORY_SURVEY:
                return new SurveyListPresenter(mContext, mCategoryIndex);
            default:
                return new CategoryListPresenter(mContext, mCategoryIndex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化action 布局
     */
    private void initTitleView() {
        // 从个人中心进入，或者报名和学习计划，不予显示已读未读
        if (!mReceivedIntent.getBooleanExtra(KEY_IS_DONE, true) && mCategoryIndex != Category.CATEGORY_ENTRY && mCategoryIndex != Category.CATEGORY_PLAN && mCategoryIndex != Category.CATEGORY_SURVEY) {
            mTitleReadTextView = new TextView(mContext);
            mTitleReadTextView.setText(R.string.category_unread);
            mTitleReadTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.getInstance().getTextSizeSmall());
            int paddingLess = DensityUtil.getInstance().getOffsetLess();
            mTitleReadTextView.setPadding(paddingLess, 0, paddingLess, 0);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, paddingLess, 0);
            mTitleReadTextView.setLayoutParams(layoutParams);
            addTitleRightView(mTitleReadTextView, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onUnreadClick();
                }
            });
            setUnread(false);
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTitleLayout = inflater.inflate(R.layout.actionbar_progress, null);
        setTitleCustomView(mTitleLayout);
        mTitleTriangleImageView = (ImageView) mTitleLayout.findViewById(R.id.iv_actionbar_triangle);
        mTitleTriangleImageView.setVisibility(View.VISIBLE);
        mTitleTextView = (TextView) mTitleLayout.findViewById(R.id.tv_actionbar_title);
        mTitleTextView.setText(Category.getCategoryName(mContext, mCategoryIndex));
        mTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onClassificationStatusChanged();
            }
        });
        switch (mCategoryIndex) {
            case Category.CATEGORY_NOTICE:
                mNoneResultView.setImageResource(R.drawable.background_none_result_notice);
                break;
            case Category.CATEGORY_TRAINING:
                mNoneResultView.setImageResource(R.drawable.background_none_result_training);
                break;
            case Category.CATEGORY_EXAM:
                mNoneResultView.setImageResource(R.drawable.background_none_result_exam);
                break;
            case Category.CATEGORY_TASK:
                mNoneResultView.setImageResource(R.drawable.background_none_result_task);
                break;
            case Category.CATEGORY_SURVEY:
                mNoneResultView.setImageResource(R.drawable.background_none_result_survey);
                break;
            default:
                mNoneResultView.setImageResource(R.drawable.background_none_result_task);
        }
    }

    private void initClassificationView() {
        mMainClassificationAdapter = new ClassificationAdapter(mContext, true);
        mMoreClassificationAdapter = new ClassificationAdapter(mContext, false);
        mClassificationMoreList.setAdapter(mMoreClassificationAdapter);
        mClassificationMainList.setAdapter(mMainClassificationAdapter);
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        if (mCategoryIndex == Category.CATEGORY_TRAINING || mCategoryIndex == Category.CATEGORY_SHELF) {
            return new VerticalSpaceItemDecoration(DensityUtil.getInstance().getOffsetLless());
        } else {
            return new DividerItemDecoration(mContext);
        }
    }

    private void initListView() {
        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(getItemDecoration());
        mContentListView.setHasFixedSize(true);

        mCategoryAdapter = CategoryAdapterFactory.getAdapter(mContext, mCategoryIndex, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mPresenter.onItemClick(mCategoryAdapter.getItem(position), position);
            }
        });

        mContentListView.setAdapter(mCategoryAdapter);
        mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                mPresenter.loadMore();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.refresh();
            }
        });
    }

    @OnTouch(R.id.classification_background)
    boolean onClassificationBackgroundTouched(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            mPresenter.onClassificationStatusChanged();
        return true;
    }

    @OnItemClick(R.id.classification_main_list)
    void onMainClassificationClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mMainClassificationAdapter.setSelectedPosition(arg2);
        mPresenter.onClassificationMainClicked(mMainClassificationAdapter.getItem(arg2));
    }

    @OnItemClick(R.id.classification_more_list)
    void onMoreClassificationClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mMoreClassificationAdapter.setSelectedPosition(arg2);
        mPresenter.onClassificationMoreClicked(mMoreClassificationAdapter.getItem(arg2));
    }

    @Override
    public void setMainClassification(List<Classification> data) {
        mMainClassificationAdapter.setSelectedPosition(0);
        mClassificationMoreList.setVisibility(View.GONE);
        mMainClassificationAdapter.setList(data);
    }

    @Override
    public void setMoreClassification(List<Classification> data) {
        mMoreClassificationAdapter.setSelectedPosition(0);
        mClassificationMoreList.setVisibility(View.VISIBLE);
        mMoreClassificationAdapter.setList(data);
    }

    @Override
    public void showNoneResult() {
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    @Override
    public void enablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mPtrClassicFrameLayout.autoRefresh();
    }

    @Override
    public boolean isRefreshing() {
        return mPtrClassicFrameLayout.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        hideProgressBar();
        mPtrClassicFrameLayout.refreshComplete();
    }

    @Override
    public void setData(List<Category> data) {
        mCategoryAdapter.setList(data);
    }

    @Override
    public void addData(List<Category> data) {
        mCategoryAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mCategoryAdapter.getItemCount();
    }

    @Override
    public void setItem(int index, Category item) {
        mCategoryAdapter.setItem(index, item);
    }

    @Override
    public Category getItem(int index) {
        return mCategoryAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mCategoryAdapter.remove(index);
    }

    public void showMenu() {
        mTitleTriangleImageView.setImageResource(R.drawable.icon_triangle_up);
        mClassificationBackground.setVisibility(View.VISIBLE);
        mClassificationContainer.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter);
        mClassificationContainer.startAnimation(anim);
    }

    public void hideMenu() {
        mTitleTriangleImageView.setImageResource(R.drawable.icon_triangle_down);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit);
        mClassificationContainer.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mClassificationBackground.setVisibility(View.INVISIBLE);
                mClassificationContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setUnread(boolean unread) {
        if (unread) {
            mTitleReadTextView.setTextColor(getResources().getColor(R.color.color_white));
            mTitleReadTextView.setBackgroundResource(R.drawable.background_button_enable_blue_normal);
        } else {
            mTitleReadTextView.setTextColor(getResources().getColor(R.color.color_border_grey));
            mTitleReadTextView.setBackgroundResource(R.drawable.background_border_radius_small_grey);
        }
    }

}
