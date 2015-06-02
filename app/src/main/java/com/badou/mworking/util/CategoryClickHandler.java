package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.NoticeActivity;
import com.badou.mworking.PDFViewerActivity;
import com.badou.mworking.R;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainVideoPlayerAct;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Notice;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.widget.HorizontalProgressDialog;

import org.holoeverywhere.app.Activity;

import java.io.File;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CategoryClickHandler {

    public static HorizontalProgressDialog progressDialog;
    public static Handler handler;
    public final static int PROGRESS_MAX = 1;
    public final static int PROGRESS_CHANGE = 2;

    public static boolean categoryClicker(Context context, Category category) {
        if (category.getClass().equals(Train.class)) {
            Train train = (Train) category;
            int subtype = train.subtype;

            if (handler == null) {
                handler = new DownloadHandler(context);
            }
            if (progressDialog == null) {
                progressDialog = new HorizontalProgressDialog(context);
            }


            if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) { //返回PDF格式
                goPDFAndWeb(context, train);
            } else if (Constant.MWKG_FORAMT_TYPE_MPEG == subtype) { // 返回MP4格式
                goVedio(context, train);
            } else if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) { // 返回html格式
                goHTML(context, train);
            } else if (Constant.MWKG_FORAMT_TYPE_MP3 == subtype) { // 返回MP3格式
                goAudio(context, train);
            } else {
                return false;
            }
        } else if (category.getClass().equals(Notice.class)) {
            Notice notice = (Notice) category;
            int subtype = notice.subtype;

            handler = new NoticeDownloadHandler(context);

            if (progressDialog == null) {
                progressDialog = new HorizontalProgressDialog(context);
            }


            // 返回类型是 html
            if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) {
                // 因为是html，所以只有联网才可以点击查看
                if (NetUtils.isNetConnected(context)) {
                    showDetail(context, notice);
                } else {
                    ToastUtil.showNetExc(context);
                }
                // 返回类型是pdf
            } else if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) {
                toPDFAndWeb(context, notice);
            } else {
                return false;
            }
        }
        return true;
    }


    private static void toPDFAndWeb(Context context, Notice notice) {
        // 声明pdf文件要保存的路径
        if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
            ToastUtil.showToast(context, R.string.train_sd_size_);
            return;
        }
        String path = FileUtils.getTrainCacheDir(context) + notice.rid + ".pdf";
        File file = new File(path);
        // pdf文件不存在
        if (!file.exists() || !file.isFile() || file.isDirectory()
                || file.length() == 0) {
            file.delete();
            // 显示对话框
            progressDialog.show();
            if (NetUtils.isNetConnected(context)) {
                // 开启线程
                new NoticeDownloadThread(context, notice).start();
            } else {
                if (progressDialog != null && progressDialog.isShowing()
                        && !((Activity) context).isFinishing()) {
                    // 关闭进度条对话框
                    progressDialog.dismiss();
                }
                ToastUtil.showToast(context, R.string.error_service);
            }
        } else {
            // pdf文件已存在 调用
            toPdfViewer(context, notice);
        }
    }


    /**
     * 功能描述: 下载pdf文件的线程
     */
    static class NoticeDownloadThread extends Thread {
        private Notice notice;
        private String path;
        private Context mContext;

        public NoticeDownloadThread(Context context, Notice notice) {
            super();
            this.mContext = context;
            this.notice = notice;
        }

        @Override
        public void run() {
            super.run();
            if (Constant.MWKG_FORAMT_TYPE_PDF == notice.subtype) {
                path = FileUtils.getTrainCacheDir(mContext) + notice.rid + ".pdf";
            }
            if (TextUtils.isEmpty(path)) {
                return;
            }
            // 通过url下载pdf文件
            int status = HttpDownloader.downFile(notice.url
                            + "&uid="
                            + ((AppApplication) mContext.getApplicationContext())
                            .getUserInfo().userId, path,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(handler, PROGRESS_CHANGE,
                                    downloadSize).sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(handler, PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            // 下载成功,向handler传递消息
            Message msg = new Message();
            msg.what = status;
            Bundle bundle = new Bundle();
            bundle.putSerializable("notice", notice);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    static class NoticeDownloadHandler extends Handler {

        private Context mContext;

        public NoticeDownloadHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 接受线程中传递的消息
            int statu = msg.what;
            Bundle bundle = msg.getData();
            Notice notice = (Notice) bundle.getSerializable("notice");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (notice != null) {
                path = FileUtils.getTrainCacheDir(mContext) + notice.rid + ".pdf";
                if (path == null || path.equals("")) {
                    return;
                }
                File file = new File(path);
                if (statu == -1 || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(mContext,
                            R.string.train_result_download_fail);
                } else {
                    if (statu != 0) {
                        ToastUtil.showToast(mContext,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == notice.subtype) {
                        // 下载完成 调用
                        toPdfViewer(mContext, notice);
                    }
                }
            }
            switch (msg.what) {
                case PROGRESS_MAX:
                    progressDialog.setProgressMax((int) msg.obj);
                    break;
                case PROGRESS_CHANGE:
                    // 设置进度条改变
                    progressDialog.setProgress((int) msg.obj);
                    break;

                default:
                    break;
            }

        }
    }

    ;


    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    private static void toPdfViewer(Context context, Notice notice) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, notice);
        intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
        intent.putExtra(PDFViewerActivity.KEY_WEBVIEW_PDF, true);
        intent.setClass(context, PDFViewerActivity.class);
        context.startActivity(intent);
    }

    /**
     * 功能描述: 显示通知的详细内容
     *
     * @param context
     * @param notice
     */
    private static void showDetail(Context context, Notice notice) {
        int subtype = notice.subtype;
        if (Constant.MWKG_FORAMT_TYPE_HTML != subtype) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, BackWebActivity.class);
        intent.putExtra(BackWebActivity.VALUE_URL, notice.url
                + "&uid="
                + ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId);
        // 获取分类名
        String title = SP.getStringSP(context, SP.NOTICE, notice.tag + "", "");
        intent.putExtra(BackWebActivity.VALUE_TITLE, title);
        intent.putExtra(BackWebActivity.VALUE_RID, notice.rid);
        intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
        BackWebActivity.PAGEFLAG = BackWebActivity.NOTICE;    //设置跳转是通知公告
        context.startActivity(intent);
    }


    public static void goVedio(Context context, Train train) {
        Intent intentToMusic = new Intent(context, TrainVideoPlayerAct.class);
        Bundle bu = new Bundle();
        bu.putSerializable(TrainVideoPlayerAct.KEY_CATEGORY_VALUE, train);
        intentToMusic.putExtra(TrainVideoPlayerAct.KEY_CATEGORY_VALUE, bu);
        context.startActivity(intentToMusic);
    }

    public static void goHTML(Context context, Train train) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("train", train);
        intent.putExtra("train", bundle);
        String url = train.url + "&uid="
                + ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId;
        intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
        intent.putExtra(BackWebActivity.VALUE_URL, url);
        // 获取分类名
        String title = SP.getStringSP(context, SP.TRAINING, train.tag + "", "");
        intent.putExtra(BackWebActivity.VALUE_TITLE, title);
        intent.setClass(context, BackWebActivity.class);
        BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
        context.startActivity(intent);
    }

    public static void goAudio(Context context, Train train) {
        Intent intentToMusic = new Intent(context, TrainMusicActivity.class);
        Bundle bu = new Bundle();
        bu.putSerializable(TrainMusicActivity.KEY_CATEGORY_VALUE, train);
        intentToMusic.putExtra(TrainMusicActivity.KEY_CATEGORY_VALUE, bu);
        context.startActivity(intentToMusic);
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    public static void goPdfView(Context context, Train train) {
        if (!((Activity) context).isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, train);
                intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
                intent.setClass(context, PDFViewerActivity.class);
                context.startActivity(intent);
            }
        }
    }

    public static void goPDFAndWeb(Context context, Train train) {
        /**
         * 判断api,太小用web
         * 判断是pdf还是web
         */
        if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
            // 声明pdf文件要保存的路径
            if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
                ToastUtil.showToast(context, R.string.train_sd_size_);
                return;
            }
            String path = FileUtils.getTrainCacheDir(context) + train.rid + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                file.delete();
                // 显示对话框
                progressDialog.show();
                if (NetUtils.isNetConnected(context)) {
                    // 开启线程
                    new DownloadThread(context, train).start();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()
                            && !((Activity) context).isFinishing()) {
                        // 关闭进度条对话框
                        progressDialog.dismiss();
                    }
                    ToastUtil.showToast(context, R.string.error_service);
                }
            } else {
                // pdf文件已存在 调用
                goPdfView(context, train);
            }
        } else {// web
            Intent intent = new Intent(context, PDFViewerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, train);
            intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
            intent.putExtra(PDFViewerActivity.KEY_WEBVIEW_PDF, false);
            context.startActivity(intent);
        }
    }

    private static class DownloadHandler extends Handler {

        private Context mContext;

        public DownloadHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 如果activity关闭，则不作任何处理
            if (((Activity) mContext).isFinishing()) {
                return;
            }
            // 接受线程中传递的消息
            if (msg.what == PROGRESS_MAX) {
                // 设置进度条最大值
                progressDialog.setProgressMax((int) msg.obj);
            } else if (msg.what == PROGRESS_CHANGE) {
                // 设置进度条改变
                progressDialog.setProgress((int) msg.obj);
            } else {
                if (progressDialog.isShowing()
                        && !((Activity) mContext).isFinishing()) {
                    // 关闭进度条对话框
                    progressDialog.dismiss();
                }
                downloadFinish(msg);
            }
        }

        private void downloadFinish(Message msg) {
            int status = msg.what;
            Bundle bundle = msg.getData();
            Train train = (Train) bundle.getSerializable("train");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (train != null) {
                if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                    path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".pdf";
                } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.subtype) {
                    path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".mp4";
                }
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                if (status == HttpDownloader.STATU_FAIL || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(mContext,
                            R.string.train_result_download_fail);
                } else {
                    if (status != HttpDownloader.STATU_SUCCESS) {
                        ToastUtil.showToast(mContext,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                        // 下载完成 调用
                        goPdfView(mContext, train);
                    }
                }
            }
        }
    }

    /**
     * 下载pdf文件的线程
     */
    static class DownloadThread extends Thread {
        private Train train;
        private String mPath;
        private Context mContext;

        public DownloadThread(Context context, Train train) {
            super();
            this.mContext = context;
            this.train = train;
        }

        @Override
        public void run() {
            super.run();
            if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                mPath = FileUtils.getTrainCacheDir(mContext) + train.rid + ".pdf";
            }
            if (mPath == null || mPath.equals("")) {
                return;
            }
            // 通过url下载pdf文件
            int statu = HttpDownloader.downFile(train.url
                            + "&uid="
                            + ((AppApplication) mContext.getApplicationContext())
                            .getUserInfo().userId, mPath,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(handler, PROGRESS_CHANGE,
                                    downloadSize).sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(handler, PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            // 下载成功,向handler传递消息
            Message msg = new Message();
            msg.what = statu;
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
