package com.badou.mworking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomRatingAndCommentView;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.joanzapata.pdfview.PDFView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 功能描述: pdf 显示页面
 */
public class PDFViewerActivity extends BaseStatisticalActionBarActivity {

    public static final String KEY_SHOW_RATING = "rating";
    public static final String KEY_SHOW_COMMENT = "comment";
    public static final String KEY_URL = "url";

    public static HorizontalProgressDialog progressDialog;

    private PDFView mPdfView;
    private BottomRatingAndCommentView mBottomView;
    private String mFilePath;
    private HttpHandler mHttpHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        initView();
        initData();

    }

    protected void initView() {
        mPdfView = (PDFView) this.findViewById(R.id.pdfview_activity_pdf_view);
        mBottomView = (BottomRatingAndCommentView) findViewById(R.id.bracv_activity_pdf_view);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        boolean showRating = mReceivedIntent.getBooleanExtra(KEY_SHOW_RATING, false);
        if (showRating) {
            mBottomView.setData(mRid, true, true);
        } else {// 设置-1，用于隐藏评分选项
            mBottomView.setData(mRid, false, true);
        }
        mBottomView.updateData();
        // 声明pdf文件要保存的路径
        mFilePath = FileUtils.getTrainCacheDir(mContext) + mRid + ".pdf";
        File file = new File(mFilePath);
        if (file.exists()) {
            showPdf();
        } else {
            startDownload(mReceivedIntent.getStringExtra(KEY_URL));
        }
    }

    private void showPdf() {
        // 显示pdf
        File file = null;
        try {
            file = new File(mFilePath);
            if (file.exists() && file.length() > 0) {
                //加载pdf文件
                mPdfView.fromFile(file).showMinimap(false).enableSwipe(true).load();
                /*mPdfView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPdfView.jumpTo(SPUtil.getPdfPage(mContext, mRid));
                    }
                }, 1000);*/
            } else {
                file.delete();
                finish();
                ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
            }
            final File finalFile = file;
            mPdfView.setOnOpenPDFFailedListener(new PDFView.OnOpenPDFFailedListener() {
                @Override
                public void onOpenFailed() {
                    if (finalFile != null) {
                        finalFile.delete();
                    }
                    finish();
                    ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
                }
            });
        } catch (Exception e) {
            // 捕获打开pdf异常
            e.printStackTrace();
            if (file != null) {
                file.delete();
            }
            ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
        }
    }

    private void startDownload(String url) {
        if (TextUtils.isEmpty(mFilePath)) {
            ToastUtil.showToast(mContext, R.string.train_result_download_memory_error);
            return;
        }
        if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
            ToastUtil.showToast(mContext, R.string.train_sd_size_limited);
            return;
        }
        progressDialog = new HorizontalProgressDialog(mContext);
        progressDialog.show();
        HttpUtils http = new HttpUtils();
        mHttpHandler = http.download(url, mFilePath + ".tmp",
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
                        progressDialog.dismiss();
                        FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), mRid + ".pdf.tmp", mRid + ".pdf");
                        showPdf();
                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        new File(mFilePath).delete();
                        ToastUtil.showToast(mContext, R.string.train_result_download_fail);
                        progressDialog.dismiss();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 更新回复数
        mBottomView.updateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.setPdfPage(mContext, mRid, mPdfView.getCurrentPage() + 1);
        if (mHttpHandler != null) {
            mHttpHandler.cancel();
        }
    }
}
