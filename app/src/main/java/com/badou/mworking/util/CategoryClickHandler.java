package com.badou.mworking.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ExamWebViewActivity;
import com.badou.mworking.NoticePDFViewActivity;
import com.badou.mworking.NoticeWebViewActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskActivity;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainPDFViewActivity;
import com.badou.mworking.TrainVideoActivity;
import com.badou.mworking.TrainWebViewActivity;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Task;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.ServiceProvider;

import java.io.File;

public class CategoryClickHandler {

    public static Intent getIntent(Context context, Category category) {
        ServiceProvider.doMarkRead(context, category.rid);
        if (category.getCategoryType() == Category.CATEGORY_NOTICE) {
            return goNoticeActivity(context, (Notice) category);
        } else if (category.getCategoryType() == Category.CATEGORY_TRAINING || category.getCategoryType() == Category.CATEGORY_SHELF) {
            return goTrainingAndShelfActivity(context, (Train) category);
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
        if (notice.subtype == Constant.MWKG_FORAMT_TYPE_PDF) {
            // 判断api,太小用web
            if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
                // pdf文件已存在 调用
                return NoticePDFViewActivity.getIntent(context, notice);
            } else {// web
                String company = SP.getStringSP(context, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
                final String webPdfUrl = Constant.TRAIN_IMG_SHOW + company + File.separator + notice.rid + Constant.TRAIN_IMG_FORMAT;
                notice.url = webPdfUrl;
                return NoticeWebViewActivity.getIntent(context, notice);
            }
        } else if (notice.subtype == Constant.MWKG_FORAMT_TYPE_HTML) {
            return NoticeWebViewActivity.getIntent(context, notice);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }

    private static Intent goTrainingAndShelfActivity(Context context, Train train) {
        if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) { //返回PDF格式
            // 判断api,太小用web
            if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
                // pdf文件已存在 调用
                return TrainPDFViewActivity.getIntent(context, train);
            } else {// web
                String company = SP.getStringSP(context, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
                final String webPdfUrl = Constant.TRAIN_IMG_SHOW + company + File.separator + train.rid + Constant.TRAIN_IMG_FORMAT;
                train.url = webPdfUrl;
                return TrainWebViewActivity.getIntent(context, train);
            }
        } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.subtype) { // 返回MP4格式
            return TrainVideoActivity.getIntent(context, train);
        } else if (Constant.MWKG_FORAMT_TYPE_HTML == train.subtype) { // 返回html格式
            return TrainWebViewActivity.getIntent(context, train);
        } else if (Constant.MWKG_FORAMT_TYPE_MP3 == train.subtype) { // 返回MP3格式
            return TrainMusicActivity.getIntent(context, train);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
            return null;
        }
    }

    private static Intent goExamActivity(Context context, Exam exam) {
        return ExamWebViewActivity.getIntent(context, exam);
    }

    private static Intent goSignActivity(Context context, Task task) {
        return TaskSignActivity.getIntent(context, task);
    }
}
