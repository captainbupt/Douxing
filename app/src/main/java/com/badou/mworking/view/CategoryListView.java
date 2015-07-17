package com.badou.mworking.view;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Classification;

import java.util.List;

public interface CategoryListView extends BaseListView<Category>, BaseActionBarView {
    void showMenu();

    void hideMenu();

    void setMainClassification(List<Classification> data);

    void setMoreClassification(List<Classification> data);

    void setUnread(boolean isUnread);

}
