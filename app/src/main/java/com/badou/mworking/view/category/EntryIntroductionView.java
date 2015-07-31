package com.badou.mworking.view.category;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.view.BaseView;

public interface EntryIntroductionView extends BaseView {
    void setData(CategoryDetail categoryDetail);

    void setStatusText(int buttonResId, boolean isEnable, int statusResId);
}
