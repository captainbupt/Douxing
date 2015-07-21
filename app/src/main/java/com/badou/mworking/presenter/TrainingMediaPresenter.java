package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.badou.mworking.R;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.TrainMediaView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrainingMediaPresenter extends Presenter {

    public String suffix = ".mp4"; // MP4后缀

    TrainMediaView mTrainMediaView;
    String mSaveFilePath;
    String mUrl;
    String mRid;
    Handler mHandler;

    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;

    public TrainingMediaPresenter(Context context, String rid, String url, int format) {
        super(context);
        mRid = rid;
        mUrl = url;
        if (format == Constant.MWKG_FORAMT_TYPE_MP3) {
            suffix = ".mp3";
        } else {
            suffix = ".mp4";
        }
    }

    @Override
    public void attachView(BaseView v) {
        mTrainMediaView = (TrainMediaView) v;
        setData();
    }

    public void startDownload() {
        if (!NetUtils.isNetConnected(mContext)) {
            mTrainMediaView.showToast(R.string.error_service);
            return;
        }
        mTrainMediaView.statusDownloading();
        ServiceProvider.doDownloadTrainingFile(mUrl, mSaveFilePath, new RangeFileAsyncHttpResponseHandler(new File(mSaveFilePath)) {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                mTrainMediaView.setProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                mTrainMediaView.showToast(R.string.error_service);
                mTrainMediaView.statusNotDownLoad();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                mTrainMediaView.statusDownloadFinish(file);
            }
        });
    }

    public void fileCrashed() {
        File file = new File(mSaveFilePath);
        if (file.exists()) {
            file.delete();
        }
        mTrainMediaView.showToast(R.string.tips_audio_error);
        mTrainMediaView.statusNotDownLoad();
    }

    public void statusChange(boolean isPlaying) {
        if (isPlaying) {
            pausePlayer();
        } else {
            startPlay();
        }
    }

    public void onProgressChanged(int progress, boolean fromUser) {
        if (fromUser) {
            mTrainMediaView.setPlayTime(progress, true);
        }
    }

    public void onRotationChanged(boolean isVertical) {
        if (isVertical) {
            mTrainMediaView.showVerticalView();
        } else {
            mTrainMediaView.showHorizontalView();
        }
    }

    public void setData() {
        mSaveFilePath = FileUtils.getTrainCacheDir(mContext) + mRid + suffix;
        File file = new File(mSaveFilePath);
        // 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
        if (file.exists()) {
            mTrainMediaView.statusDownloadFinish(file);
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
                            mTrainMediaView.setFileSize(fileSize);
                        }
                    });
                } catch (Exception e) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTrainMediaView.showToast(R.string.tips_audio_get_size_fail);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        mTrainMediaView.statusNotDownLoad();
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            if (!mTrainMediaView.isVertical() && mTrainMediaView.isPlaying()) {
                mTrainMediaView.setProgressBar(false);
            }
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    };

    private Runnable changeStatusRunnable = new Runnable() {
        @Override
        public void run() {
            mTrainMediaView.setPlayTime(mTrainMediaView.getCurrentTime(), false);
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
        mTrainMediaView.startPlay();
    }

    private void pausePlayer() {
        if (mHandler == null)
            mHandler = new Handler();
        mHandler.removeCallbacks(hideRunnable);
        mHandler.removeCallbacks(changeStatusRunnable);
        mTrainMediaView.stopPlay();
    }

    public void pause() {
        pausePlayer();
    }

    public void destroy() {
        pausePlayer();
        ServiceProvider.cancelRequest(mContext);
    }
}
