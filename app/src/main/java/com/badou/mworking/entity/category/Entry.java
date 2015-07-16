package com.badou.mworking.entity.category;

public class Entry extends Category {

    int maxusr;
    long deadline_c;
    long startline_c;
    int enroll;
    int in;
    String description;

    @Override
    public int getCategoryType() {
        return Category.CATEGORY_ENTRY;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
        this.read = categoryDetail.entry.in;
    }

    public int getRead() {
        return read;
    }
}