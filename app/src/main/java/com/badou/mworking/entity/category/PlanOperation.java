package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

/**
 * Created by badou1 on 2015/7/30.
 */
public class PlanOperation {
    @SerializedName("rid")
    String rid;
    @SerializedName("categoryDetail")
    CategoryDetail categoryDetail;

    public PlanOperation(String rid, CategoryDetail categoryDetail) {
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
