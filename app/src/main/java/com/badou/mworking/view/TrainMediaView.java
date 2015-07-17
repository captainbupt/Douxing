package com.badou.mworking.view;

import java.io.File;

public interface TrainMediaView extends CategoryBaseView {
    void statusNotDownLoad();

    void statusDownloadFinish(File filePath);

    void statusDownloading();

    void setProgress(long bytesWritten, long totalSize);

    void showVerticalView();

    void showHorizontalView();

    void setPlayTime(int time, boolean isVideo);

    void setFileSize(float fileSize);

    void setProgressBar(boolean visible);

    void startPlay();

    void stopPlay();

    int getCurrentTime();

    boolean isPlaying();

    boolean isVertical();
}
