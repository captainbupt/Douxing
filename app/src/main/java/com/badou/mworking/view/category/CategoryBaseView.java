package com.badou.mworking.view.category;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.PlanInfo;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.StoreItemView;

public interface CategoryBaseView extends BaseView, StoreItemView {

    void setActionbarTitle(String title);

    void setData(String rid, CategoryDetail categoryDetail, PlanInfo planInfo);

    void setCommentNumber(int number);

    void setRatingNumber(int number);

    void setRated(boolean rated);

    void showTimingView();

    void setStore(boolean isStore);

    void setMaxPeriod(int minute);

    void setCurrentPeriod(int currentSecond);
}
