package com.badou.mworking.view;

import com.badou.mworking.entity.category.CategoryDetail;

public interface CategoryBaseView extends BaseView {

    void setData(String rid, CategoryDetail categoryDetail);

    void setCommentNumber(int number);

    void setRatingNumber(int number);

    void setStore(boolean isStore);
}
