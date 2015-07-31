package com.badou.mworking.factory;

import android.content.Context;

import com.badou.mworking.adapter.EntryAdapter;
import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.adapter.PlanAdapter;
import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;

public class CategoryAdapterFactory {

    public static MyBaseAdapter<Category> getAdapter(Context context, int category) {
        switch (category) {
            case Category.CATEGORY_NOTICE:
                return new NoticeAdapter(context);
            case Category.CATEGORY_EXAM:
                return new ExamAdapter(context);
            case Category.CATEGORY_TRAINING:
                return new TrainAdapter(context);
            case Category.CATEGORY_TASK:
                return new TaskAdapter(context);
            case Category.CATEGORY_SHELF:
                return new TrainAdapter(context);
            case Category.CATEGORY_ENTRY:
                return new EntryAdapter(context);
            case Category.CATEGORY_PLAN:    //学习计划适配器
            return new PlanAdapter(context);
            default:
                return new NoticeAdapter(context);
        }
    }

}
