package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.ExamWebViewActivity;
import com.badou.mworking.NoticePDFViewActivity;
import com.badou.mworking.NoticeWebViewActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainPDFViewActivity;
import com.badou.mworking.TrainVideoActivity;
import com.badou.mworking.TrainWebViewActivity;
import com.badou.mworking.domain.MarkReadUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.entity.category.Task;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.ServiceProvider;

import java.io.File;

public class CategoryClickHandler {

    public static Intent getIntent(Context context, Category category) {
        if (category.getCategoryType() == Category.CATEGORY_NOTICE || category.getCategoryType() == Category.CATEGORY_TRAINING || category.getCategoryType() == Category.CATEGORY_SHELF) {
            if (category.isUnread()) {
                SPHelper.reduceUnreadNumberByOne(category.getCategoryType());
            }
            new MarkReadUseCase(category.getRid()).execute(new BaseSubscriber(context) {
                @Override
                public void onResponseSuccess(Object data) {
                }
            });
        }
        if (category.getCategoryType() == Category.CATEGORY_NOTICE) {
            return goNoticeActivity(context, (Notice) category);
        } else if (category.getCategoryType() == Category.CATEGORY_TRAINING || category.getCategoryType() == Category.CATEGORY_SHELF) {
            return goTrainingAndShelfActivity(context, (Train) category, category.getCategoryType() == Category.CATEGORY_TRAINING);
        } else if (category.getCategoryType() == Category.CATEGORY_EXAM) {
            return goExamActivity(context, (Exam) category);
        } else if (category.getCategoryType() == Category.CATEGORY_TASK) {
            return goSignActivity(context, (Task) category);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }

    private static Intent goNoticeActivity(Context context, Notice notice) {
        if (notice.getSubtype() == Constant.MWKG_FORAMT_TYPE_PDF) {
            // 判断api,太小用web
            if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
                // pdf文件已存在 调用
                return NoticePDFViewActivity.getIntent(context, notice.getRid());
            } else {// web
                return NoticeWebViewActivity.getIntent(context, notice.getRid());
            }
        } else if (notice.getSubtype() == Constant.MWKG_FORAMT_TYPE_HTML) {
            return NoticeWebViewActivity.getIntent(context, notice.getRid());
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }

    private static Intent goTrainingAndShelfActivity(Context context, Train train, boolean isTraining) {
        if (Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()) { //返回PDF格式
            // 判断api,太小用web
            if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
                // pdf文件已存在 调用
                return TrainPDFViewActivity.getIntent(context, train.getRid(), isTraining);
            } else {// web
                return TrainWebViewActivity.getIntent(context, train.getRid(), isTraining);
            }
        } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.getSubtype()) { // 返回MP4格式
            return TrainVideoActivity.getIntent(context, train.getRid(), isTraining);
        } else if (Constant.MWKG_FORAMT_TYPE_HTML == train.getSubtype()) { // 返回html格式
            return TrainWebViewActivity.getIntent(context, train.getRid(), isTraining);
        } else if (Constant.MWKG_FORAMT_TYPE_MP3 == train.getSubtype()) { // 返回MP3格式
            return TrainMusicActivity.getIntent(context, train.getRid(), isTraining);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }

    private static Intent goExamActivity(Context context, Exam exam) {
        return ExamWebViewActivity.getIntent(context, exam.getRid());
    }

    private static Intent goSignActivity(Context context, Task task) {
        return TaskSignActivity.getIntent(context, task.getRid());
    }
}
