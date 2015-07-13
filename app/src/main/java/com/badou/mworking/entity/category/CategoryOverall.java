package com.badou.mworking.entity.category;

import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class CategoryOverall {
    @Expose
    int ttlcnt;
    @Expose
    int newcnt;
    @Expose
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
            Class clz;
            switch (category) {
                case Category.CATEGORY_NOTICE:
                    clz = Notice.class;
                    break;
                case Category.CATEGORY_TRAINING:
                    clz = Train.class;
                    break;
                case Category.CATEGORY_EXAM:
                    clz = Exam.class;
                    break;
                case Category.CATEGORY_SHELF:
                    clz = Train.class;
                    break;
                case Category.CATEGORY_TASK:
                    clz = Task.class;
                    break;
                default:
                    clz = Notice.class;
            }
            categoryList = GsonUtil.fromLinedTreeMap(list, clz);
        }
        return categoryList;
    }
}
