package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.badou.mworking.R;
import com.badou.mworking.net.HttpClientRepository;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.DownloadView;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class DownloadPresenter extends Presenter {

    public String suffix = ".mp4"; // MP4后缀

    DownloadView mDownloadView;
    String mSaveFilePath;
    String mUrl;
    String mRid;
    Handler mHandler;
    OnStatusChangedListener mOnStatusChangedListener;

    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;

    public DownloadPresenter(Context context, String rid, String url, int format) {
        super(context);
        mRid = rid;
        mUrl = url;
        if (format == Constant.MWKG_FORAMT_TYPE_MP3) {
            suffix = ".mp3";
        } else if (format == Constant.MWKG_FORAMT_TYPE_MPEG) {
            suffix = ".mp4";
        } else if (format == Constant.MWKG_FORAMT_TYPE_PDF) {
            suffix = ".pdf";
        }
    }

    @Override
    public void attachView(BaseView v) {
        mDownloadView = (DownloadView) v;
        setData();
    }

    public void startDownload() {
        if (!NetUtils.isNetConnected(mContext)) {
            mDownloadView.showToast(R.string.error_service);
            return;
        }
        mDownloadView.statusDownloading();
        HttpClientRepository.doDownloadTrainingFile(mContext, mUrl, mSaveFilePath, new HttpClientRepository.DownloadListener() {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                mDownloadView.setProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                mDownloadView.showToast(R.string.error_service);
                mDownloadView.statusNotDownLoad();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                mDownloadView.statusDownloadFinish(file);
            }
        });
    }

    public void fileCrashed() {
        File file = new File(mSaveFilePath);
        if (file.exists()) {
            file.delete();
        }
        mDownloadView.showToast(R.string.tips_audio_error);
        mDownloadView.statusNotDownLoad();
    }

    public void statusChange(boolean isPlaying) {
        if (isPlaying) {
            pausePlayer();
        } else {
            startPlay();
        }
    }

    public interface OnStatusChangedListener {
        void onStatusChanged(boolean isPlaying);
    }

    public void setOnStatusChangedListener(OnStatusChangedListener onStatusChangedListener) {
        mOnStatusChangedListener = onStatusChangedListener;
    }

    public void onProgressChanged(int progress, boolean fromUser) {
        if (fromUser) {
            mDownloadView.setPlayTime(progress, true);
        }
    }

    public void onRotationChanged(boolean isVertical) {
        if (isVertical) {
            mDownloadView.showVerticalView();
        } else {
            mDownloadView.showHorizontalView();
        }
    }

    public void setData() {
        mSaveFilePath = FileUtils.getTrainCacheDir(mContext) + mRid + suffix;
        File file = new File(mSaveFilePath);
        // 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
        if (file.exists()) {
            mDownloadView.statusDownloadFinish(file);
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL DownRul = new URL(mUrl);
                    HttpURLConnection urlcon = (HttpURLConnection) DownRul.openConnection();
                    final float fileSize = ((float) urlcon.getContentLength()) / 1024f / 1024f;
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadView.setFileSize(fileSize);
                        }
                    });
                } catch (Exception e) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadView.showToast(R.string.tips_audio_get_size_fail);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        mDownloadView.statusNotDownLoad();
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            if (!mDownloadView.isVertical() && mDownloadView.isPlaying()) {
                mDownloadView.setProgressBar(false);
            }
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    };

    private Runnable changeStatusRunnable = new Runnable() {
        @Override
        public void run() {
            mDownloadView.setPlayTime(mDownloadView.getCurrentTime(), false);
            mHandler.postDelayed(changeStatusRunnable, 1000);
        }
    };

    /**
     * 功能描述: 视屏开始播放
     */
    private void startPlay() {
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.postDelayed(hideRunnable, HIDE_TIME);
        mHandler.post(changeStatusRunnable);
        mDownloadView.startPlay();
        if (mOnStatusChangedListener != null) {
            mOnStatusChangedListener.onStatusChanged(true);
        }
    }

    private void pausePlayer() {
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.removeCallbacks(hideRunnable);
        mHandler.removeCallbacks(changeStatusRunnable);
        mDownloadView.stopPlay();
        if (mOnStatusChangedListener != null) {
            mOnStatusChangedListener.onStatusChanged(false);
        }
    }

    public void pause() {
        pausePlayer();
    }

    public void destroy() {
        pausePlayer();
        HttpClientRepository.cancelRequest(mContext);
    }
}
