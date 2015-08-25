package com.badou.mworking.presenter.category;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.category.CategoryUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.net.BaseSubscriber;

import java.util.List;

public class ExamListPresenter extends CategoryListPresenter {
    public ExamListPresenter(Context context, int category) {
        super(context, category);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mClickPosition >= 0 && mClickPosition < mCategoryListView.getDataCount()) {
            final Category category = mCategoryListView.getItem(mClickPosition);
            CategoryUseCase categoryUseCase = new CategoryUseCase(category.getCategoryType());
            categoryUseCase.setItemNum(1);
            categoryUseCase.setPageNum(mClickPosition + 1);
            categoryUseCase.execute(new BaseSubscriber<CategoryOverall>(mContext) {
                @Override
                public void onResponseSuccess(CategoryOverall data) {
                    List<Category> categoryList = data.getCategoryList(category.getCategoryType());
                    if (categoryList != null && categoryList.size() == 1) {
                        mCategoryListView.setItem(mClickPosition, categoryList.get(0));
                    }
                }
            });
        }
    }
}
