package com.badou.mworking.factory;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.EntryActivity;
import com.badou.mworking.ExamBaseActivity;
import com.badou.mworking.NoticeBaseActivity;
import com.badou.mworking.PlanActivity;
import com.badou.mworking.R;
import com.badou.mworking.SurveyBaseActivity;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainBaseActivity;
import com.badou.mworking.domain.category.MarkReadUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;

public class CategoryIntentFactory {

    public static Intent getIntent(Context context, int type, String rid) {
        return getIntent(context, type, rid, false, null);
    }

    public static Intent getIntentForPlan(Context context, int type, String rid, String title) {
        return getIntent(context, type, rid, false, title);
    }

    public static Intent getIntent(Context context, int type, String rid, boolean isUnread, String planTitle) {
        if (type == Category.CATEGORY_NOTICE || type == Category.CATEGORY_TRAINING || type == Category.CATEGORY_SHELF) {
            if (isUnread) {
                SPHelper.reduceUnreadNumberByOne(type);
            }
            new MarkReadUseCase(rid).execute(new BaseSubscriber(context) {
                @Override
                public void onResponseSuccess(Object data) {
                }
            });
        }
        if (type == Category.CATEGORY_NOTICE) {
            return NoticeBaseActivity.getIntent(context, rid, planTitle);
        } else if (type == Category.CATEGORY_TRAINING || type == Category.CATEGORY_SHELF) {
            return TrainBaseActivity.getIntent(context, rid, type == Category.CATEGORY_TRAINING, planTitle);
        } else if (type == Category.CATEGORY_EXAM) {
            return ExamBaseActivity.getIntent(context, rid, planTitle);
        } else if (type == Category.CATEGORY_TASK) {
            return TaskSignActivity.getIntent(context, rid, planTitle);
        } else if (type == Category.CATEGORY_ENTRY) {
            return EntryActivity.getIntent(context, rid, planTitle);
        } else if (type == Category.CATEGORY_PLAN) {//学习计划跳转
            return PlanActivity.getIntent(context, rid, planTitle);
        } else if (type == Category.CATEGORY_SURVEY) {//学习计划跳转
            return SurveyBaseActivity.getIntent(context, rid, planTitle);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }
}
