package com.badou.mworking.view;

import com.badou.mworking.entity.category.CategoryDetail;

public interface EntryIntroductionView extends BaseView {
    void setData(CategoryDetail categoryDetail);

    void setStatusText(int buttonResId, boolean isEnable, int statusResId);
}
