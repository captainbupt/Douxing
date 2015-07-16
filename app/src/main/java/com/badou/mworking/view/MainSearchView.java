package com.badou.mworking.view;

import com.badou.mworking.entity.category.CategorySearch;

public interface MainSearchView extends BaseListView<CategorySearch>{
    void clear();

    void hideFocus();

    void setFocus();
}
