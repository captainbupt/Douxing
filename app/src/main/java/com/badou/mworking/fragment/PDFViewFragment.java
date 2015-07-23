package com.badou.mworking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.presenter.DownloadPresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.view.DownloadView;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.joanzapata.pdfview.PDFView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PDFViewFragment extends BaseFragment implements DownloadView {

    private static final String KEY_RID = "rid";
    private static final String KEY_URL = "url";

    @Bind(R.id.pdf_view)
    PDFView mPdfView;

    boolean isFirst = true;
    private HorizontalProgressDialog mHorizontalProgressDialog;
    DownloadPresenter mPresenter;

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
        String rid = bundle.getString(KEY_RID);
        String url = bundle.getString(KEY_URL);
        mPresenter = new DownloadPresenter(mContext, rid, url, Constant.MWKG_FORAMT_TYPE_PDF);
        mPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        mPresenter.destroy();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void statusNotDownLoad() {
        setProgressBar(false);
        if (isFirst) {
            isFirst = false;
            mPresenter.startDownload();
        }
    }

    @Override
    public void statusDownloadFinish(File file) {
        setProgressBar(false);
        // 显示pdf
        try {
            if (file.exists() && file.length() > 0) {
                //加载pdf文件
                mPdfView.fromFile(file).showMinimap(false).enableSwipe(true).load();
            } else {
                mPresenter.fileCrashed();
            }
            mPdfView.setOnOpenPDFFailedListener(new PDFView.OnOpenPDFFailedListener() {
                @Override
                public void onOpenFailed() {
                    mPresenter.fileCrashed();
                }
            });
        } catch (Exception e) {
            // 捕获打开pdf异常
            e.printStackTrace();
            mPresenter.fileCrashed();
        }
    }

    @Override
    public void statusDownloading() {
        setProgressBar(true);
    }

    @Override
    public void setProgress(long bytesWritten, long totalSize) {
        if (mHorizontalProgressDialog != null) {
            if (mHorizontalProgressDialog.getProgressMax() != (int) totalSize)
                mHorizontalProgressDialog.setProgressMax((int) totalSize);
            mHorizontalProgressDialog.setProgress((int) bytesWritten);
        }
    }

    @Override
    public void showVerticalView() {

    }

    @Override
    public void showHorizontalView() {

    }

    @Override
    public void setPlayTime(int time, boolean isVideo) {

    }

    @Override
    public void setFileSize(float fileSize) {

    }

    @Override
    public void setProgressBar(boolean visible) {
        if (visible) {
            if (mHorizontalProgressDialog == null)
                mHorizontalProgressDialog = new HorizontalProgressDialog(mContext);
            mHorizontalProgressDialog.show();
        } else {
            if (mHorizontalProgressDialog != null && mHorizontalProgressDialog.isShowing()) {
                mHorizontalProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void startPlay() {

    }

    @Override
    public void stopPlay() {

    }

    @Override
    public int getCurrentTime() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isVertical() {
        return false;
    }
}
