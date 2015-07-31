package com.badou.mworking.entity.category;

/**
 * Created by badou1 on 2015/7/28.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plan extends Category {
    @SerializedName("maxusr")
    int maxusr;
    @SerializedName("deadline_c")
    long deadline_c;
    @SerializedName("startline_c")
    long startline_c;
    @SerializedName("enroll")
    int enroll;
    @SerializedName("in")
    int in;
    @SerializedName("description")
    String description;

    @Expose
    @SerializedName("desc")//課程計劃描述
         String desc;

    @Expose
    @SerializedName("deadline")//課程計劃描述
            String deadline;


    //學習計劃描述
    public String getDesc() {
        return desc;
    }
    public String getPlanDeadline() {
        return deadline;
    }
    @Override
    public int getCategoryType() {
        return Category.CATEGORY_PLAN;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
      //  this.read = categoryDetail.entry.in;
    }

        public int getRead() {
        return read;
    }
}