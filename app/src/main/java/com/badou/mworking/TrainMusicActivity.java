package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.CategoryBasePresenter;
import com.badou.mworking.presenter.TrainingMediaPresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.TrainMediaView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrainMusicActivity extends TrainBaseActivity implements TrainMediaView {

    @Bind(R.id.music_title_text_view)
    TextView mMusicTitleTextView;
    @Bind(R.id.file_size_text_view)
    TextView mFileSizeTextView;
    @Bind(R.id.download_image_view)
    ImageView mDownloadImageView;
    @Bind(R.id.downloading_progress_bar)
    ProgressBar mDownloadingProgressBar;
    @Bind(R.id.player_control_image_view)
    ImageView mPlayerControlImageView;
    @Bind(R.id.current_time_text_view)
    TextView mCurrentTimeTextView;
    @Bind(R.id.progress_seek_bar)
    SeekBar mProgressSeekBar;
    @Bind(R.id.total_time_text_view)
    TextView mTotalTimeTextView;

    private MediaPlayer mMusicPlayer = null;

    TrainingMediaPresenter mPresenter;

    public static Intent getIntent(Context context, String rid, boolean isTraining) {
        return TrainBaseActivity.getIntent(context, TrainMusicActivity.class, rid, isTraining);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        mPresenter = (TrainingMediaPresenter) super.mPresenter;
        mPresenter.attachView(this);
        initListener();
    }

    /**
     * 功能描述: 监听初始化
     */
    private void initListener() {

        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPresenter.onProgressChanged(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public CategoryBasePresenter getPresenter() {
        boolean isTraining = mReceivedIntent.getBooleanExtra(KEY_TRAINING, true);
        return new TrainingMediaPresenter(mContext, isTraining ? Category.CATEGORY_TRAINING : Category.CATEGORY_SHELF, mReceivedIntent.getStringExtra(KEY_RID), Constant.MWKG_FORAMT_TYPE_MP3);
    }

    @OnClick(R.id.download_image_view)
    void onDownloadImageClicked() {
        mPresenter.startDownload();
    }

    @OnClick(R.id.player_control_image_view)
    void onControlClicked() {
        mPresenter.statusChange(mMusicPlayer.isPlaying());
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        super.setData(rid, categoryDetail);
        mMusicTitleTextView.setText(categoryDetail.getSubject());
    }

    /**
     * 没下载的状态 *
     */
    @Override
    public void statusNotDownLoad() {
        mTotalTimeTextView.setText("0%");
        mDownloadImageView.setVisibility(View.VISIBLE);
        mPlayerControlImageView.setVisibility(View.GONE);
        mCurrentTimeTextView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.GONE);
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    /**
     * 下载完成,可以播放的状态 *
     */
    @Override
    public void statusDownloadFinish(File file) {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.VISIBLE);
        mCurrentTimeTextView.setVisibility(View.VISIBLE);
        mDownloadingProgressBar.setVisibility(View.GONE);
        mProgressSeekBar.setEnabled(true);
        mProgressSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_));
        float fileSize = ((float) file.length()) / 1024f / 1024f;
        setFileSize(fileSize);
        initMedia(file);
    }

    /**
     * 下载中 *
     */
    @Override
    public void statusDownloading() {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.GONE);
        mCurrentTimeTextView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.VISIBLE);
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    /**
     * 功能描述:初始化播放器
     */
    private void initMedia(File file) {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MediaPlayer();
        }
        try {
            mMusicPlayer.reset();
            mMusicPlayer.setDataSource(file.getAbsolutePath());
            mMusicPlayer.prepare();// 就是把存储卡中的内容全部加载或者网络中的部分媒体内容加载到内存中，有可能会失败抛出异常的
        } catch (Exception e) {
            e.printStackTrace();
            mPresenter.fileCrashed();
            return;
        }
        mProgressSeekBar.setMax(mMusicPlayer.getDuration());// 设置进度条
        mProgressSeekBar.setProgress(0);
        mTotalTimeTextView.setText(new SimpleDateFormat("mm:ss").format(mMusicPlayer.getDuration()));
        mCurrentTimeTextView.setText("00:00");
    }

    @Override
    public void setProgress(long bytesWritten, long totalSize) {
        if (mProgressSeekBar.getMax() != (int) totalSize)
            mProgressSeekBar.setMax((int) totalSize);
        mProgressSeekBar.setProgress((int) bytesWritten);
        if (totalSize > 0 && bytesWritten > 0) {
            mTotalTimeTextView.setText(100 * bytesWritten / totalSize + "%");
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
        if (isVideo)
            mMusicPlayer.seekTo(time);
        mProgressSeekBar.setProgress(time);
        mCurrentTimeTextView.setText(new SimpleDateFormat("mm:ss").format(time));
    }

    @Override
    public void setFileSize(float fileSize) {
        mFileSizeTextView.setText(String.format("视频文件（%.1fM）", fileSize));
    }

    @Override
    public void setProgressBar(boolean visible) {
    }

    @Override
    public void startPlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_stop);
        mMusicPlayer.start();// 开始
    }

    @Override
    public void stopPlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
        mMusicPlayer.pause();
    }

    @Override
    public int getCurrentTime() {
        try {
            return mMusicPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        return mMusicPlayer.isPlaying();
    }

    @Override
    public boolean isVertical() {
        return true;
    }

    @Override
    protected void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    // 来电处理
    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        mMusicPlayer.release();
        super.onDestroy();
    }
}
