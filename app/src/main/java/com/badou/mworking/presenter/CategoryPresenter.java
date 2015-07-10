package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.domain.CategoryUseCase;
import com.badou.mworking.domain.ClassificationUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseListSubscriber;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.CategoryListView;

import java.util.List;

public abstract class CategoryPresenter extends ListPresenter<Category> {
    protected final int REQUEST_DETAIL = 1;

    private CategoryListView mCategoryListView;
    private CategoryUseCase mCategoryUseCase;

    public CategoryPresenter(Context context) {
        super(context);
    }

    abstract protected int getCategoryIndex();

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mCategoryListView = (CategoryListView) v;
    }

    @Override
    protected UseCase getLoadMoreUseCase() {
        if (mCategoryUseCase == null)
            mCategoryUseCase = new CategoryUseCase(getCategoryIndex());
        mCategoryUseCase.setBegin(mCurrentIndex + 1);
        return mCategoryUseCase;
    }

    @Override
    protected UseCase getRefreshUseCase() {
        if (mCategoryUseCase == null)
            mCategoryUseCase = new CategoryUseCase(getCategoryIndex());
        mCategoryUseCase.setBegin(1);
        return mCategoryUseCase;
    }

    @Override
    public void loadMore() {
        mBaseListView.hideNoneResult();
        mBaseListView.setRefreshing();
        getRefreshUseCase().execute(new BaseSubscriber<CategoryOverall<Category>>(mContext) {
            @Override
            public void onResponseSuccess(CategoryOverall<Category> data) {
                mCurrentIndex++;
                setList(data.getCategoryList());

            }

            @Override
            public void onCompleted() {
                mBaseListView.refreshComplete();
            }
        });
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    protected void toDetailPage(Category category) {
        mActivity.startActivityForResult(CategoryClickHandler.getIntent(mContext, category), REQUEST_DETAIL);
    }


    // 功能描述: 设置已读
    protected void setRead(Category category) {
        if (category.isAvailable()) {
            SPHelper.reduceUnreadNumberByOne(getCategoryIndex());
        }
    }

    // 功能描述:通过网络获取 类别 列表
    private void getClassifications() {
        mCategoryListView.setClassification(SPHelper.getClassification(Category.CATEGORY_KEY_NAMES[getCategoryIndex()]));
        new ClassificationUseCase(Category.CATEGORY_KEY_NAMES[getCategoryIndex()]).execute(new BaseListSubscriber<Classification>(mContext) {

            @Override
            public void onResponseSuccess(List<Classification> data) {
                SPHelper.setClassification(Category.CATEGORY_KEY_NAMES[getCategoryIndex()], data);
                mCategoryListView.setClassification(data);
            }
        });
    }

    public void onClassificationClicked(Classification classification) {
        mCategoryListView.hideMenu();
        mCategoryUseCase.setTag(classification.getTag());
        mCategoryListView.refreshComplete();
        mCategoryListView.setRefreshing();

    }

    private void setUnread(boolean unread) {
        mCategoryUseCase.setDone(unread ? CategoryUseCase.TYPE_UNREAD : CategoryUseCase.TYPE_ALL);
        mCategoryListView.refreshComplete();
        mCategoryListView.setRefreshing();
    }

}
