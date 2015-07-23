package com.badou.mworking.fragment;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.presenter.DownloadPresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.view.DownloadView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrainMusicFragment extends BaseFragment implements DownloadView {

    private static final String KEY_RID = "rid";
    private static final String KEY_URL = "url";
    static final String KEY_SUBJECT = "subject";

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

    DownloadPresenter mPresenter;

    public static TrainMusicFragment getFragment(String rid, String url, String subject) {
        TrainMusicFragment trainMusicFragment = new TrainMusicFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        argument.putString(KEY_URL, url);
        argument.putString(KEY_SUBJECT, subject);
        trainMusicFragment.setArguments(argument);
        return trainMusicFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_music_player, container, false);
        ButterKnife.bind(this, view);
        initListener();
        Bundle argument = getArguments();
        mMusicTitleTextView.setText(argument.getString(KEY_SUBJECT));
        mPresenter = new DownloadPresenter(mContext, argument.getString(KEY_RID), argument.getString(KEY_URL), Constant.MWKG_FORAMT_TYPE_MP3);
        mPresenter.attachView(this);
        return view;
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

    @OnClick(R.id.download_image_view)
    void onDownloadImageClicked() {
        mPresenter.startDownload();
    }

    @OnClick(R.id.player_control_image_view)
    void onControlClicked() {
        mPresenter.statusChange(mMusicPlayer.isPlaying());
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
    public void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    // 来电处理
    @Override
    public void onDestroy() {
        mPresenter.destroy();
        mMusicPlayer.release();
        ButterKnife.unbind(this);
        super.onDestroy();
    }


}
