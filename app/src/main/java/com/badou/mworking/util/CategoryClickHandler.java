package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.PDFViewerActivity;
import com.badou.mworking.R;
import com.badou.mworking.TrainMusicActivity;
import com.badou.mworking.TrainVideoPlayerAct;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.badou.mworking.widget.WaitProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.holoeverywhere.app.Activity;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2015/5/29.
 */
public class CategoryClickHandler {

    public static HorizontalProgressDialog progressDialog;
    public static DownloadHandler handler;
    public final static int PROGRESS_MAX = 11;
    public final static int PROGRESS_CHANGE = 12;
    public final static int PROGRESS_FINISH = 13;
    public final static int PROGRESS_FAILED = 14;

    public static void categoryClicker(Context context, Category category) {
        goNextPage(context, category.getClassificationName(context), category.getCategoryType(), category.subtype, category.rid, category.url + "&uid="
                + ((AppApplication) context.getApplicationContext())
                .getUserInfo().userId, category.subject);
    }

    public static boolean categoryClicker(final Context context, final int type, final String rid, final String subject) {
        ServiceProvider.getResourceDetail(context, rid, new VolleyListener(context) {
            @Override
            public void onResponseData(JSONObject jsonObject) {
                CategoryDetail detail = new CategoryDetail(jsonObject);
                goNextPage(context, detail.tagName, type, detail.format, rid, detail.url, subject);
            }

        });
        return true;
    }

    public static void goNextPage(Context context, String tagName, int type, int subtype, String rid, String url, String subject) {

        if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) { //返回PDF格式
            goPDFAndWeb(context, type, rid, url, tagName);
        } else if (Constant.MWKG_FORAMT_TYPE_MPEG == subtype) { // 返回MP4格式
            goVedio(context, rid, subject);
        } else if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) { // 返回html格式
            goHTML(context, rid, url, tagName);
        } else if (Constant.MWKG_FORAMT_TYPE_MP3 == subtype) { // 返回MP3格式
            goAudio(context, rid, subject);
        } else {
            ToastUtil.showToast(context, R.string.category_unsupport_type);
        }
    }

    /**
     * 功能描述: 显示HTML的详细内容
     */
    private static void goHTML(Context context, String rid, String url, String title) {
        Intent intent = new Intent();
        intent.setClass(context, BackWebActivity.class);
        intent.putExtra(BackWebActivity.KEY_URL, url);
        // 获取分类名
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        intent.putExtra(BackWebActivity.KEY_RID, rid);
        intent.putExtra(BackWebActivity.KEY_STATISTICAL, true);
        context.startActivity(intent);
    }

    /**
     * 功能描述: 显示视频的详细内容
     */
    public static void goVedio(Context context, String rid, String title) {
        Intent intent = new Intent(context, TrainVideoPlayerAct.class);
        intent.putExtra(TrainVideoPlayerAct.KEY_RID, rid);
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, title);
        context.startActivity(intent);
    }

    public static void goAudio(Context context, String rid, String title) {
        Intent intent = new Intent(context, TrainMusicActivity.class);
        intent.putExtra(TrainMusicActivity.KEY_RID, rid);
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
                intent.putExtra(PDFViewerActivity.KEY_SHOW_RATING, type == Category.CATEGORY_TRAIN);
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
                ToastUtil.showToast(context, R.string.train_sd_size_);
                return;
            }
            String path = FileUtils.getTrainCacheDir(context) + rid + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                file.delete();
                // 显示对话框
                if (handler == null) {
                    handler = new DownloadHandler();
                }
                if (progressDialog == null) {
                    progressDialog = new HorizontalProgressDialog(context);
                }
                progressDialog.show();
                if (NetUtils.isNetConnected(context)) {
                    // 开启线程
                    new DownloadThread(context, type, rid, url, title).start();
                } else {
                    if (progressDialog.isShowing() && !((Activity) context).isFinishing()) {
                        // 关闭进度条对话框
                        progressDialog.dismiss();
                    }
                    ToastUtil.showToast(context, R.string.error_service);
                }
            } else {
                // pdf文件已存在 调用
                goPdfView(context, type, rid, title);
            }
        } else {// web
            String company = SP.getStringSP(context, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
            final String webPdfUrl = Constant.TRAIN_IMG_SHOW + company + File.separator + rid + Constant.TRAIN_IMG_FORMAT;
            goHTML(context, rid, webPdfUrl, title);
            // getRespStatus(url);
        }
    }
/*
    private void getRespStatus(final String url) {
        if (mWebVieHandler == null) {
            mWebVieHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (mActivity.isFinishing())
                        return;
                    int status = msg.what;
                    if (status == 200) {
                        mWebView.loadUrl(url);
                    } else {
                        ToastUtil.showToast(PDFViewerActivity.this, R.string.web_error);
                    }
                }
            };
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = 0;
                try {
                    HttpGet head = new HttpGet(url);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse resp = client.execute(head);
                    status = resp.getStatusLine().getStatusCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mWebVieHandler.obtainMessage(status).sendToTarget();
            }
        }).start();
    }*/

    private static class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = (Context) msg.obj;
            // 如果activity关闭，则不作任何处理
            if (((Activity) context).isFinishing()) {
                return;
            }
            // 接受线程中传递的消息
            if (msg.what == PROGRESS_MAX) {
                // 设置进度条最大值
                progressDialog.setProgressMax((int) msg.obj);
            } else if (msg.what == PROGRESS_CHANGE) {
                // 设置进度条改变
                progressDialog.setProgress((int) msg.obj);
            } else if (msg.what == PROGRESS_FAILED) {
                ToastUtil.showToast(context,
                        R.string.train_result_download_memory_error);
            } else if (msg.what == PROGRESS_FINISH) {
                if (progressDialog.isShowing()) {
                    // 关闭进度条对话框
                    progressDialog.dismiss();
                }
                Container container = (Container) msg.obj;
                downloadFinish(container.context, container.type, container.status, container.rid, container.title);
            }
        }

        private void downloadFinish(Context context, int type, int status, String rid, String title) {
            String path = "";
            // 声明文件保存路径 用rid命名
            path = FileUtils.getTrainCacheDir(context) + rid + ".pdf";
            if (TextUtils.isEmpty(path)) {
                ToastUtil.showToast(context,
                        R.string.train_result_download_memory_error);
                return;
            }
            File file = new File(path);
            if (status == HttpDownloader.STATU_FAIL || !file.exists() || file.length() == 0) {
                // 文件下载失败 提示
                ToastUtil.showToast(context,
                        R.string.train_result_download_fail);
            } else {
                if (status != HttpDownloader.STATU_SUCCESS) {
                    ToastUtil.showToast(context,
                            R.string.train_result_download_exist);
                }
                // 下载完成 调用
                goPdfView(context, type, rid, title);
            }
        }
    }

    /**
     * 下载pdf文件的线程
     */
    static class DownloadThread extends Thread {
        private Context mContext;
        private String mRid;
        private String mUrl;
        private String mTitle;
        private int mType;

        public DownloadThread(Context context, int type, String rid, String url, String title) {
            this.mContext = context;
            this.mUrl = url;
            this.mRid = rid;
            this.mTitle = title;
            this.mType = type;
        }

        @Override
        public void run() {
            super.run();
            String path = FileUtils.getTrainCacheDir(mContext) + mRid + ".pdf";
            if (TextUtils.isEmpty(path)) {
                handler.sendEmptyMessage(PROGRESS_FAILED);
                return;
            }
            // 通过url下载pdf文件
            int status = HttpDownloader.downFile(mUrl, path,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(handler, PROGRESS_MAX, downloadSize)
                                    .sendToTarget();
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
            msg.what = PROGRESS_FINISH;
            msg.obj = new Container(mContext, status, mType, mRid, mTitle);
            handler.sendMessage(msg);
        }
    }

    static class Container {
        public Context context;
        public int status;
        public int type;
        public String rid;
        public String title;

        public Container(Context context, int type, int status, String rid, String title) {
            this.context = context;
            this.status = status;
            this.type = type;
            this.rid = rid;
            this.title = title;
        }
    }
}
