package com.badou.mworking.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.joanzapata.pdfview.PDFView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PDFViewFragment extends BaseFragment {

    public static final String KEY_RID = "rid";
    public static final String KEY_URL = "url";

    @Bind(R.id.pdf_view)
    PDFView mPdfView;

    private String mRid;
    private String mFilePath;
    private HorizontalProgressDialog progressDialog;

    public static Bundle getArgument(String rid, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RID, rid);
        bundle.putString(KEY_URL, url);
        return bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_view, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = getArguments();
        mRid = bundle.getString(KEY_RID);
        String mUrl = bundle.getString(KEY_URL);
        // 声明pdf文件要保存的路径
        mFilePath = FileUtils.getTrainCacheDir(mContext) + mRid + ".pdf";
        File file = new File(mFilePath);
        if (file.exists()) {
            showPdf();
        } else {
            startDownload(mUrl);
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
                mActivity.finish();
                ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
            }
            final File finalFile = file;
            mPdfView.setOnOpenPDFFailedListener(new PDFView.OnOpenPDFFailedListener() {
                @Override
                public void onOpenFailed() {
                    finalFile.delete();
                    mActivity.finish();
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
        ServiceProvider.doDownloadTrainingFile(url, mFilePath, new RangeFileAsyncHttpResponseHandler(new File(mFilePath)) {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if (progressDialog.getProgressMax() != (int) totalSize)
                    progressDialog.setProgressMax((int) totalSize);
                progressDialog.setProgress((int) bytesWritten);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                ToastUtil.showToast(mContext, R.string.train_result_download_fail);
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progressDialog.dismiss();
                showPdf();
            }
        });
    }

    @Override
    public void onDestroyView() {
        SPHelper.setPdfPage(mRid, mPdfView.getCurrentPage() + 1);
        ServiceProvider.cancelRequest(mContext);
        progressDialog.dismiss();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
