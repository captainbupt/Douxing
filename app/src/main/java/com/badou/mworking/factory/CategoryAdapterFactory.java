package com.badou.mworking.factory;

import android.content.Context;
import android.view.View;

import com.badou.mworking.adapter.CategoryBaseAdapter;
import com.badou.mworking.adapter.EntryAdapter;
import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.adapter.PlanAdapter;
import com.badou.mworking.adapter.SurveyAdapter;
import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.category.Category;

public class CategoryAdapterFactory {

    public static CategoryBaseAdapter getAdapter(Context context, int category, View.OnClickListener onClickListener) {
        switch (category) {
            case Category.CATEGORY_NOTICE:
                return new NoticeAdapter(context, onClickListener);
            case Category.CATEGORY_EXAM:
                return new ExamAdapter(context, onClickListener);
            case Category.CATEGORY_TRAINING:
                return new TrainAdapter(context, onClickListener);
            case Category.CATEGORY_TASK:
                return new TaskAdapter(context, onClickListener);
            case Category.CATEGORY_SHELF:
                return new TrainAdapter(context, onClickListener);
            case Category.CATEGORY_ENTRY:
                return new EntryAdapter(context, onClickListener);
            case Category.CATEGORY_PLAN:    //学习计划适配器
                return new PlanAdapter(context, onClickListener);
            case Category.CATEGORY_SURVEY:
                return new SurveyAdapter(context, onClickListener);
            default:
                return new NoticeAdapter(context, onClickListener);
        }
    }

}
