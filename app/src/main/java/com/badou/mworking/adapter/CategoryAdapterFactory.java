package com.badou.mworking.adapter;

import android.content.Context;

import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;

public class CategoryAdapterFactory {

    public static MyBaseAdapter<Category> getAdapter(Context context, int category){
        switch (category){
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
            default:
                return new NoticeAdapter(context);
        }
    }

}
