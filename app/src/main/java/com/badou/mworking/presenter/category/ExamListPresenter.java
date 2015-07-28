package com.badou.mworking.presenter.category;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.net.BaseSubscriber;

public class ExamListPresenter extends CategoryListPresenter {
    public ExamListPresenter(Context context, int category) {
        super(context, category);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mClickPosition >= 0 && mClickPosition < mCategoryListView.getDataCount()) {
            final Exam exam = (Exam) mCategoryListView.getItem(mClickPosition);
            new CategoryDetailUseCase(exam.getRid()).execute(new BaseSubscriber<CategoryDetail>(mContext) {
                @Override
                public void onResponseSuccess(CategoryDetail data) {
                    exam.updateData(data);
                    mCategoryListView.setItem(mClickPosition, exam);
                }
            });
        }
    }
}
