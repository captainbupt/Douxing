package com.badou.mworking.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.PDFViewerActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskActivity;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainVideoActivity;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.model.MainBanner;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CategoryClickHandler {

    public static void categoryClicker(Context context, CategoryDetail categoryDetail) {
        goNextPage(context, categoryDetail);
        ServiceProvider.doMarkRead(context, categoryDetail.rid);
    }

    public static void goNextPage(Context context, CategoryDetail categoryDetail) {
        String title = MainIcon.getMainIcon(context, Category.CATEGORY_KEY_ICONS[categoryDetail.type]).name;
        if (categoryDetail.type == Category.CATEGORY_NOTICE || categoryDetail.type == Category.CATEGORY_TRAINING || categoryDetail.type == Category.CATEGORY_SHELF) {
            if (Constant.MWKG_FORAMT_TYPE_PDF == categoryDetail.format) { //返回PDF格式
                goPDFAndWeb(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, title);
            } else if (Constant.MWKG_FORAMT_TYPE_MPEG == categoryDetail.format) { // 返回MP4格式
                goVedio(context, categoryDetail.rid, title, categoryDetail.url, categoryDetail.subject);
            } else if (Constant.MWKG_FORAMT_TYPE_HTML == categoryDetail.format) { // 返回html格式
                goHTML(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, title);
            } else if (Constant.MWKG_FORAMT_TYPE_MP3 == categoryDetail.format) { // 返回MP3格式
                goAudio(context, categoryDetail.rid, title, categoryDetail.url, categoryDetail.subject);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        } else if (categoryDetail.type == Category.CATEGORY_EXAM) {
            if (Constant.MWKG_FORAMT_TYPE_XML == categoryDetail.format) {
                goHTML(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, title);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        } else if (categoryDetail.type == Category.CATEGORY_TASK) {
            if (Constant.MWKG_FORAMT_TYPE_XML == categoryDetail.format) {
                goSignActivity(context, title, categoryDetail.task);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        }
    }

    private static void goSignActivity(Context context, String title, Task task) {
        Intent intent = new Intent(context, TaskSignActivity.class);
        intent.putExtra(TaskSignActivity.KEY_TASK, task);
        // 获取分类名
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        if (context.getClass().equals(TaskActivity.class)) {
            ((TaskActivity) context).startActivityForResult(intent, 1);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 功能描述: 显示HTML的详细内容
     */
    private static void goHTML(Context context, int type, String rid, String url, String title) {
        Intent intent = new Intent();
        intent.setClass(context, BackWebActivity.class);
        intent.putExtra(BackWebActivity.KEY_URL, url);
        // 获取分类名
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        intent.putExtra(BackWebActivity.KEY_RID, rid);
        intent.putExtra(BackWebActivity.KEY_SHOW_STATISTICAL, true);
        intent.putExtra(BackWebActivity.KEY_SHOW_RATING, type == Category.CATEGORY_TRAINING || type == Category.CATEGORY_SHELF);
        intent.putExtra(BackWebActivity.KEY_SHOW_COMMENT, type == Category.CATEGORY_TRAINING || type == Category.CATEGORY_SHELF || type == Category.CATEGORY_NOTICE);
        context.startActivity(intent);
    }

    /**
     * 功能描述: 显示视频的详细内容
     */
    public static void goVedio(Context context, String rid, String title, String url, String subject) {

        Intent intent = new Intent(context, TrainVideoActivity.class);
        intent.putExtra(TrainVideoActivity.KEY_URL, url);
        intent.putExtra(TrainVideoActivity.KEY_SUBJECT, subject);
        intent.putExtra(TrainVideoActivity.KEY_RID, rid);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }

    public static void goAudio(Context context, String rid, String title, String url, String subject) {
        Intent intent = new Intent(context, TrainMusicActivity.class);
        intent.putExtra(TrainMusicActivity.KEY_URL, url);
        intent.putExtra(TrainMusicActivity.KEY_SUBJECT, subject);
        intent.putExtra(TrainMusicActivity.KEY_RID, rid);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    public static void goPdfView(Context context, int type, String rid, String title, String url) {
        if (!((Activity) context).isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent(context, PDFViewerActivity.class);
                intent.putExtra(PDFViewerActivity.KEY_RID, rid);
                intent.putExtra(PDFViewerActivity.KEY_SHOW_RATING, type == Category.CATEGORY_TRAINING || type == Category.CATEGORY_SHELF);
                intent.putExtra(PDFViewerActivity.KEY_SHOW_COMMENT, true);
                intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
                intent.putExtra(PDFViewerActivity.KEY_URL, url);
                context.startActivity(intent);
            }
        }
    }

    public static void goPDFAndWeb(Context context, int type, String rid, String url, String title) {
        /**
         * 判断api,太小用web
         * 判断是pdf还是web
         */
        if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
            // pdf文件已存在 调用
            goPdfView(context, type, rid, title, url);
        } else {// web
            String company = SP.getStringSP(context, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
            final String webPdfUrl = Constant.TRAIN_IMG_SHOW + company + File.separator + rid + Constant.TRAIN_IMG_FORMAT;
            goHTML(context, type, rid, webPdfUrl, title);
        }
    }
}
