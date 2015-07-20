package com.badou.mworking.entity.category;

public class EntryOperation {
    String rid;
    CategoryDetail categoryDetail;

    public EntryOperation(String rid, CategoryDetail categoryDetail) {
        this.rid = rid;
        this.categoryDetail = categoryDetail;
    }

    public String getRid() {
        return rid;
    }

    public CategoryDetail getCategoryDetail() {
        return categoryDetail;
    }

    public void setCategoryDetail(CategoryDetail categoryDetail) {
        this.categoryDetail = categoryDetail;
    }
}
