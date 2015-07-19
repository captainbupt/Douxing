package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;

import java.util.List;

public class EntryPresenter extends CategoryBasePresenter {

    EntryIntroductionPresenter mEntryIntroductionPresenter;
    EntryOperationPresenter mEntryOperationPresenter;
    CommentPresenter mCommentPresenter;

    public EntryPresenter(Context context, String rid) {
        super(context, Category.CATEGORY_ENTRY, rid);
    }

    public void setChildPresenters(EntryIntroductionPresenter entryIntroductionPresenter, EntryOperationPresenter entryOperationPresenter, CommentPresenter commentPresenter) {
        this.mEntryIntroductionPresenter = entryIntroductionPresenter;
        this.mEntryOperationPresenter = entryOperationPresenter;
        this.mCommentPresenter = commentPresenter;
    }

    @Override
    public void setData(CategoryDetail categoryDetail) {
        super.setData(categoryDetail);
        mEntryIntroductionPresenter.setData(categoryDetail);
        mEntryOperationPresenter.setData(categoryDetail);
    }

    @Override
    public boolean onBackPressed() {
        return mCommentPresenter.onBackPressed();
    }
}
