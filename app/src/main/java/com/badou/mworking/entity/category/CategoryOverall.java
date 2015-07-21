package com.badou.mworking.entity.category;

import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class CategoryOverall {
    @SerializedName("ttlcnt")
    @Expose
    int ttlcnt;
    @SerializedName("newcnt")
    @Expose
    int newcnt;
    @Expose
    @SerializedName("list")
    List<LinkedTreeMap> list;

    transient List<Category> categoryList;

    public int getTotalCount() {
        return ttlcnt;
    }

    public int getUnreadCount() {
        return newcnt;
    }

    // Retrofit并不能很好的支持泛型自定义处理，所以需要手动处理一下
    public List<Category> getCategoryList(int category) {
        if (categoryList == null) {
            categoryList = GsonUtil.fromLinedTreeMap(list, Category.CATEGORY_KEY_CLASSES[category]);
            if (category == Category.CATEGORY_SHELF || category == Category.CATEGORY_TRAINING) {
                for (Category tmp : categoryList) {
                    ((Train) tmp).isTraining = category == Category.CATEGORY_TRAINING;
                }
            }
        }
        return categoryList;
    }
}
