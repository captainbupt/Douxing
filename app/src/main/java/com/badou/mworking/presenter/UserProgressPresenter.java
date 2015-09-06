package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;

import com.badou.mworking.CategoryListActivity;
import com.badou.mworking.domain.category.CategoryUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.factory.CategoryIntentFactory;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.category.CategoryListView;

import java.lang.reflect.Type;

public class UserProgressPresenter extends ListPresenter<Category> {

    protected CategoryListView mCategoryListView;
    protected CategoryUseCase mCategoryUseCase;
    protected final int mCategoryIndex;

    public UserProgressPresenter(Context context, int category) {
        super(context);
        mCategoryIndex = category;
    }

    @Override
    protected Type getType() {
        if (mCategoryIndex >= 0 && mCategoryIndex < Category.CATEGORY_KEY_TYPES.length) {
            return Category.CATEGORY_KEY_TYPES[mCategoryIndex];
        }
        return null;
    }

    @Override
    protected boolean setData(Object data, int index) {
        CategoryOverall categoryOverall = (CategoryOverall) data;
        SPHelper.setUnreadNumber(mCategoryIndex, categoryOverall.getUnreadCount());
        return setList(categoryOverall.getCategoryList(mCategoryIndex), index);
    }

    @Override
    protected String getCacheKey() {
        return Category.CATEGORY_KEY_UNREADS[mCategoryIndex] + "done";
    }

    @Override
    protected UseCase getRefreshUseCase(int pageNum) {
        if (mCategoryUseCase == null) {
            mCategoryUseCase = new CategoryUseCase(mCategoryIndex);
            mCategoryUseCase.setDone(CategoryUseCase.TYPE_READ);
        }
        mCategoryUseCase.setPageNum(pageNum);
        return mCategoryUseCase;
    }

    @Override
    public void toDetailPage(Category category) {
        ((Activity) mContext).startActivityForResult(CategoryIntentFactory.getIntent(mContext, category.getCategoryType(), category.getRid(), category.isUnread(), null), REQUEST_DETAIL);
    }

    public void toCategoryListPage() {
        mContext.startActivity(CategoryListActivity.getIntent(mContext, mCategoryIndex, false));
    }
}
