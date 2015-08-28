package com.badou.mworking.presenter.category;

import android.app.Activity;
import android.content.Context;

import com.badou.mworking.domain.category.CategoryUseCase;
import com.badou.mworking.domain.category.ClassificationUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.factory.CategoryIntentFactory;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.CategoryListView;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryListPresenter extends ListPresenter<Category> {

    protected CategoryListView mCategoryListView;
    protected CategoryUseCase mCategoryUseCase;

    protected final int mCategoryIndex;
    protected boolean isUnread = false;
    protected boolean isMenuShow = false;
    protected boolean isDone = false;

    public CategoryListPresenter(Context context, int category) {
        super(context);
        this.mCategoryIndex = category;
        mCategoryUseCase = new CategoryUseCase(mCategoryIndex);
    }

    @Override
    public void attachView(BaseView v) {
        mCategoryListView = (CategoryListView) v;
        getClassifications();
        super.attachView(v);
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
        if (isDone) {
            mCategoryUseCase.setDone(CategoryUseCase.TYPE_READ);
        }
    }

    @Override
    protected Type getType() {
        if (mCategoryIndex >= 0 && mCategoryIndex < Category.CATEGORY_KEY_TYPES.length) {
            return Category.CATEGORY_KEY_TYPES[mCategoryIndex];
        }
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageNum) {
        mCategoryUseCase.setPageNum(pageNum);
        return mCategoryUseCase;
    }

    @Override
    protected String getCacheKey() {
        return Category.CATEGORY_KEY_UNREADS[mCategoryIndex];
    }

    @Override
    protected boolean setData(Object data, int index) {
        CategoryOverall categoryOverall = (CategoryOverall) data;
        SPHelper.setUnreadNumber(mCategoryIndex, categoryOverall.getUnreadCount());
        return setList(categoryOverall.getCategoryList(mCategoryIndex), index);
    }

    @Override
    public void toDetailPage(Category category) {
        ((Activity) mContext).startActivityForResult(CategoryIntentFactory.getIntent(mContext, category.getCategoryType(), category.getRid(), category.isUnread(), false), REQUEST_DETAIL);
    }

    @Override
    public void onResponseItem(int position, Serializable item) {
        Category category = mCategoryListView.getItem(position);
        category.updateData((CategoryDetail) item);
        mCategoryListView.setItem(position, category);
    }

    // 功能描述:通过网络获取 类别 列表
    private void getClassifications() {
        mCategoryListView.setMainClassification(SPHelper.getClassification(Category.CATEGORY_KEY_NAMES[mCategoryIndex]));
        new ClassificationUseCase(Category.CATEGORY_KEY_NAMES[mCategoryIndex]).execute(new BaseSubscriber<List<Classification>>(mContext) {
            @Override
            public void onResponseSuccess(List<Classification> data) {
                Collections.sort(data, new Comparator<Classification>() {
                    @Override
                    public int compare(Classification lhs, Classification rhs) {
                        return -Integer.valueOf(lhs.getPriority()).compareTo(rhs.getPriority());
                    }
                });
                SPHelper.setClassification(Category.CATEGORY_KEY_NAMES[mCategoryIndex], data);
                mCategoryListView.setMainClassification(data);
            }
        });
    }

    public void onClassificationStatusChanged() {
        if (isMenuShow) {
            mCategoryListView.hideMenu();
        } else {
            mCategoryListView.showMenu();
        }
        isMenuShow = !isMenuShow;
    }

    public void onClassificationMainClicked(Classification classification) {
        mCategoryListView.setMoreClassification(classification.getSon());
        if (classification.getSon() == null || classification.getSon().size() == 0) {
            mCategoryListView.hideMenu();
            isMenuShow = false;
            mCategoryUseCase.setTag(classification.getTag());
            mCategoryListView.refreshComplete();
            mCategoryListView.showProgressBar();
            mCategoryListView.startRefreshing();
            mCategoryListView.setActionbarTitle(classification.getName());
        }
    }

    public void onClassificationMoreClicked(Classification classification) {
        mCategoryListView.hideMenu();
        isMenuShow = false;
        mCategoryUseCase.setTag(classification.getTag());
        mCategoryListView.refreshComplete();
        mCategoryListView.startRefreshing();
        mCategoryListView.setActionbarTitle(classification.getName());
    }

    public void onUnreadClick() {
        isUnread = !isUnread;
        mCategoryListView.setUnread(isUnread);
        mCategoryUseCase.setDone(isUnread ? CategoryUseCase.TYPE_UNREAD : CategoryUseCase.TYPE_ALL);
        mCategoryListView.refreshComplete();
        mCategoryListView.hideMenu();
        isMenuShow = false;
        mCategoryListView.startRefreshing();
    }
}
