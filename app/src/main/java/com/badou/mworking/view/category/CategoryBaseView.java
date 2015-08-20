package com.badou.mworking.view.category;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.StoreItemView;

public interface CategoryBaseView extends BaseView, StoreItemView {

    void setData(String rid, CategoryDetail categoryDetail);

    void setCommentNumber(int number);

    void setRatingNumber(int number);

    void hideCommentView();

    void setStore(boolean isStore);
}
