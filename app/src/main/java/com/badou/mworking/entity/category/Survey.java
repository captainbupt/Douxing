package com.badou.mworking.entity.category;

public class Survey extends Category {
    @Override
    public int getCategoryType() {
        return CATEGORY_SURVEY;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {

    }
}
