package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.PDFViewerActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainVideoActivity;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.holoeverywhere.app.Activity;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CategoryClickHandler {

    public static HorizontalProgressDialog progressDialog;
    private static HttpHandler mDownloadHandler;

    public static void categoryClicker(Context context, Category category) {
        goNextPage(context, new CategoryDetail(context, category));
        ServiceProvider.doMarkRead(context, category.rid);
    }

    public static void categoryClicker(final Context context, final int type, final String rid, final String subject) {
        ServiceProvider.getResourceDetail(context, rid, new VolleyListener(context) {
            @Override
            public void onResponseData(JSONObject jsonObject) {
                CategoryDetail detail = new CategoryDetail(context, jsonObject, type, rid, subject, null);
                goNextPage(context, detail);
                ServiceProvider.doMarkRead(context, rid);
            }
        });
    }

    public static void goNextPage(Context context, CategoryDetail categoryDetail) {
        if (categoryDetail.type == Category.CATEGORY_NOTICE || categoryDetail.type == Category.CATEGORY_TRAINING || categoryDetail.type == Category.CATEGORY_SHELF) {
            if (Constant.MWKG_FORAMT_TYPE_PDF == categoryDetail.format) { //返回PDF格式
                goPDFAndWeb(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, categoryDetail.tagName);
            } else if (Constant.MWKG_FORAMT_TYPE_MPEG == categoryDetail.format) { // 返回MP4格式
                goVedio(context, categoryDetail.rid, categoryDetail.tagName, categoryDetail.url, categoryDetail.subject);
            } else if (Constant.MWKG_FORAMT_TYPE_HTML == categoryDetail.format) { // 返回html格式
                goHTML(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, categoryDetail.tagName);
            } else if (Constant.MWKG_FORAMT_TYPE_MP3 == categoryDetail.format) { // 返回MP3格式
                goAudio(context, categoryDetail.rid, categoryDetail.tagName, categoryDetail.url, categoryDetail.subject);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        } else if (categoryDetail.type == Category.CATEGORY_EXAM) {
            if (Constant.MWKG_FORAMT_TYPE_XML == categoryDetail.format) {
                goHTML(context, categoryDetail.type, categoryDetail.rid, categoryDetail.url, categoryDetail.tagName);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        } else if (categoryDetail.type == Category.CATEGORY_TASK) {
            if (Constant.MWKG_FORAMT_TYPE_XML == categoryDetail.format) {
                goSignActivity(context, categoryDetail.task);
            } else {
                ToastUtil.showToast(context, R.string.category_unsupport_type);
            }
        }
    }

    private static void goSignActivity(Context context, Task task) {
        Intent intent = new Intent(context, TaskSignActivity.class);
        intent.putExtra(TaskSignActivity.KEY_TASK, task);
        // 获取分类名
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, context.getResources().getString(R.string.module_default_title_task));
        intent.putExtra(BaseStatisticalActionBarActivity.KEY_RID, task.rid);
        context.startActivity(intent);
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
        if (type == Category.CATEGORY_TRAINING) {
            intent.putExtra(PDFViewerActivity.KEY_SHOW_RATING, true);
            intent.putExtra(PDFViewerActivity.KEY_SHOW_COMMENT, true);
        } else if (type == Category.CATEGORY_NOTICE) {
            intent.putExtra(PDFViewerActivity.KEY_SHOW_RATING, false);
            intent.putExtra(PDFViewerActivity.KEY_SHOW_COMMENT, true);
        }
        context.startActivity(intent);
    }

    /**
     * 功能描述: 显示视频的详细内容
     */
    public static void goVedio(Context context, String rid, String title, String url, String subject) {

        Intent intent = new Intent(context, TrainVideoActivity.class);
        intent.putExtra(TrainVideoActivity.KEY_URL, url);
        intent.putExtra(TrainVideoActivity.KEY_SUBJECT, subject);
        intent.putExtra(BaseStatisticalActionBarActivity.KEY_RID, rid);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }

    public static void goAudio(Context context, String rid, String title, String url, String subject) {
        Intent intent = new Intent(context, TrainMusicActivity.class);
        intent.putExtra(TrainMusicActivity.KEY_URL, url);
        intent.putExtra(TrainMusicActivity.KEY_SUBJECT, subject);
        intent.putExtra(BaseStatisticalActionBarActivity.KEY_RID, rid);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    public static void goPdfView(Context context, int type, String rid, String title) {
        if (!((Activity) context).isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent(context, PDFViewerActivity.class);
                intent.putExtra(PDFViewerActivity.KEY_RID, rid);
                intent.putExtra(PDFViewerActivity.KEY_SHOW_RATING, type == Category.CATEGORY_TRAINING);
                intent.putExtra(PDFViewerActivity.KEY_SHOW_COMMENT, true);
                intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
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
            // 声明pdf文件要保存的路径
            if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
                ToastUtil.showToast(context, R.string.train_sd_size_limited);
                return;
            }
            String path = FileUtils.getTrainCacheDir(context) + rid + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                startDownload(context, type, rid, url, title);
            } else {
                // pdf文件已存在 调用
                goPdfView(context, type, rid, title);
            }
        } else {// web
            String company = SP.getStringSP(context, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
            final String webPdfUrl = Constant.TRAIN_IMG_SHOW + company + File.separator + rid + Constant.TRAIN_IMG_FORMAT;
            goHTML(context, type, rid, webPdfUrl, title);
        }
    }

    private static void startDownload(final Context context, final int type, final String rid, String url, final String title) {
        final String ENDWITH_PDF = ".pdf";
        if (mDownloadHandler != null) {
            ToastUtil.showToast(context, R.string.action_update_download_ing);
            return;
        }
        final String path = FileUtils.getTrainCacheDir(context) + rid + ENDWITH_PDF;
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showToast(context,
                    R.string.train_result_download_memory_error);
            return;
        }

        if (progressDialog == null) {
            progressDialog = new HorizontalProgressDialog(context);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        HttpUtils http = new HttpUtils();
        mDownloadHandler = http.download(url,
                path + ".tmp",
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (progressDialog.getProgressMax() != total)
                            progressDialog.setProgressMax((int) total);
                        progressDialog.setProgress((int) current);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        FileUtils.renameFile(FileUtils.getTrainCacheDir(context), rid + ENDWITH_PDF + ".tmp", rid + ENDWITH_PDF);
                        goPdfView(context, type, rid, title);
                        mDownloadHandler.cancel();
                        mDownloadHandler = null;
                        progressDialog.dismiss();
                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        new File(path + ".tmp").delete();
                        ToastUtil.showToast(context,
                                R.string.train_result_download_fail);
                        progressDialog.dismiss();
                    }
                });
    }
}
