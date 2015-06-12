/* 
 * 文件名: TongSHQVideoPlayActivity.java
 * 包路径: com.badou.mworking
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月16日 下午3:18:42
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月16日 下午3:18:42 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.badou.mworking.R.color;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.widget.FullScreenVideoView;
import com.badou.mworking.widget.SwipeBackLayout;

import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 功能描述: 同事圈视屏播放页面
 */
public class VideoPlayActivity extends BaseBackActionBarActivity {

    public static final String KEY_VIDEOURL = "videourl";
    public static final String KEY_VIDEOPATH = "path";

    private TextView tvCurrentTime;  // 当前时间

    private String videoURl = "";
    private String videoPath = "";
    private File fileMedia = null;

    // 自定义VideoView
    private FullScreenVideoView mVideo;

    // 播放的按钮
    private CheckBox chkStartPlay;
    private ProgressBar mProgressBar; // 进度条
    private SeekBar mSeekBar; // 播放进度条

    private static final int VIDEOPLAY = 0; //视屏正在播放

    private SwipeBackLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        init();
    }

    /**
     * 功能描述: 布局初始化
     */
    private void init() {
        mVideo = (FullScreenVideoView) this
                .findViewById(R.id.tongshiquan_video);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        chkStartPlay = (CheckBox) this.findViewById(R.id.play_btn);
        tvCurrentTime = (TextView) this.findViewById(R.id.currentTime);
        mSeekBar = (SeekBar) this.findViewById(R.id.sb_activity_music_player);
        setActionbarTitle(MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_CHATTER).name);
        videoURl = mReceivedIntent.getStringExtra(KEY_VIDEOURL);
        videoPath = mReceivedIntent.getStringExtra(KEY_VIDEOPATH);
        fileMedia = new File(videoPath);
        if (fileMedia.exists()) {
            statuDownFinish();
            mVideo.setBackgroundColor(VideoPlayActivity.this.getResources().getColor(color.transparent));
        } else {
            if (!TextUtils.isEmpty(videoURl)) {
                // 文件存在，下载完成
                statuNotDown();
                tvCurrentTime.setText("0%");
                new DownloadThread().start();
            }
        }
        chkStartPlay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    if (mVideo.isPlaying()) {
                        mVideo.pause();
                    }
                } else {
                    if (!mVideo.isPlaying()) {
                        mVideo.start();
                    }
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    int time = progress * mVideo.getDuration() / 100;
                    mVideo.seekTo(time);
                }
            }
        });
    }

    /**
     * 类: <code> DownloadThread </code> 功能描述: 下载pdf文件的线程 创建人:董奇 创建日期: 2014年7月16日
     * 上午9:30:29 开发环境: JDK7.0
     */
    class DownloadThread extends Thread {

        @Override
        public void run() {
            super.run();
            // 通过url下载pdf文件
            HttpDownloader.downFile(videoURl, fileMedia.getAbsolutePath(),
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(mHandler,
                                    TrainActivity.PROGRESS_CHANGE, downloadSize)
                                    .sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(mHandler,
                                    TrainActivity.PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            mHandler.sendEmptyMessage(TrainActivity.PROGRESS_FINISH);
        }
    }

    /**
     * 功能描述: 视屏播放
     */
    private void playVideo() {
        tvCurrentTime.setText("00:00");
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
        mVideo.setVideoPath(fileMedia.getAbsolutePath());
        mVideo.requestFocus();
        mVideo.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideo.setVideoWidth(mp.getVideoWidth());
                mVideo.setVideoHeight(mp.getVideoHeight());
                mVideo.start();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(VideoPlayActivity.VIDEOPLAY);
                    }
                }, 0, 1000);
            }
        });
        mVideo.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tvCurrentTime.setText("00:00");
                chkStartPlay.setChecked(true);
                mSeekBar.setProgress(0);
            }
        });
        mVideo.setOnTouchListener(mTouchListener);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TrainMusicActivity.STATU_START_PLAY:
                    if (mVideo.isPlaying()) {
                        if (mVideo.getCurrentPosition() > 0) {
                            tvCurrentTime.setText(formatTime(mVideo
                                    .getCurrentPosition()));
                            int progress = mVideo.getCurrentPosition() * 100
                                    / mVideo.getDuration();
                            mSeekBar.setProgress(progress);
                            if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
                                tvCurrentTime.setText("00:00");
                                mSeekBar.setProgress(0);
                            }
                            mSeekBar.setSecondaryProgress(mVideo
                                    .getBufferPercentage());
                        } else {
                            tvCurrentTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                    }
                    break;
                case TrainActivity.PROGRESS_MAX:
                    if (mSeekBar != null) {
                        mSeekBar.setMax((int) msg.obj);
                    }
                    break;
                case TrainActivity.PROGRESS_CHANGE:
                    // 设置进度条改变
                    if (mSeekBar != null && msg.obj != null) {
                        mSeekBar.setProgress((int) msg.obj);
                        if (mSeekBar.getMax() > 0) {
                            int proNum = mSeekBar.getProgress() * 100
                                    / mSeekBar.getMax();
                            tvCurrentTime.setText(proNum + "%");
                        }
                    }
                    break;
                case TrainActivity.PROGRESS_FINISH:
                    mVideo.setBackgroundColor(VideoPlayActivity.this.getResources().getColor(color.transparent));
                    System.out.println("下载完成");
                    mVideo.setVideoPath(fileMedia.toString());
                    statuDownFinish();
                    startPlay();
                    break;
                case VideoPlayActivity.VIDEOPLAY:    //视屏播放时，进度条的改变
                    if (mVideo.getCurrentPosition() > 0) {
                        tvCurrentTime.setText(formatTime(mVideo
                                .getCurrentPosition()));
                        int progress = mVideo.getCurrentPosition() * 100
                                / mVideo.getDuration();
                        mSeekBar.setProgress(progress);
                        if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
                            tvCurrentTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                        mSeekBar.setSecondaryProgress(mVideo
                                .getBufferPercentage());
                    } else {
                        tvCurrentTime.setText("00:00");
                        mSeekBar.setProgress(0);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 功能描述: 视屏开始播放
     */
    private void startPlay() {
        if (mVideo.isPlaying()) {
            mVideo.pause();
        } else {
            mVideo.start();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:    //点击事件
                    // 视屏正在播放
                    if (mVideo.isPlaying()) {
                        mVideo.pause();
                        chkStartPlay.setVisibility(View.VISIBLE);
                        chkStartPlay.setChecked(true);
                        // 视屏暂停
                    } else {
                        chkStartPlay.setChecked(false);
                        chkStartPlay.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 没有下载文件 *
     */
    private void statuNotDown() {
        chkStartPlay.setVisibility(View.GONE);
        tvCurrentTime.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mSeekBar.setEnabled(false);
        mSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    /**
     * 下载完成 *
     */
    private void statuDownFinish() {
        chkStartPlay.setVisibility(View.GONE);
        tvCurrentTime.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mSeekBar.setEnabled(true);
        mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_tong));
        playVideo();
    }
}
