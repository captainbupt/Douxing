package com.badou.mworking.presenter.category;

import android.content.Context;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.CommentPresenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.category.EntryView;

public class EntryPresenter extends CategoryBasePresenter {

    EntryIntroductionPresenter mEntryIntroductionPresenter;
    EntryOperationPresenter mEntryOperationPresenter;
    CommentPresenter mCommentPresenter;
    EntryView mEntryView;


    public EntryPresenter(Context context, String rid) {
        super(context, Category.CATEGORY_ENTRY, rid, false);
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mEntryView = (EntryView) v;
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
        if (categoryDetail.getEntry().getIn() == 2) {
            mEntryView.setSwipeEnable(true);
        } else {
            mEntryView.setSwipeEnable(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        return mCommentPresenter.onBackPressed();
    }
}
