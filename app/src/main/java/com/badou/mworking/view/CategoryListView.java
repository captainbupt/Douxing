package com.badou.mworking.view;

import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Category;

import java.util.List;

public interface CategoryListView extends BaseListView<Category> {
    void showMenu();

    void hideMenu();

    void setClassification(List<Classification> data);
}
