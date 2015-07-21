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
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.R.color;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.FullScreenVideoView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;

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
        setActionbarTitle(UserInfo.getUserInfo().getShuffle().getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_CHATTER).getName());
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
                downloadFile();
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

    private void downloadFile() {
        mProgressDialog.show();
        ServiceProvider.doDownloadTrainingFile(videoURl, videoPath, new RangeFileAsyncHttpResponseHandler(new File(videoPath)) {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (mSeekBar.getMax() != (int) totalSize) {
                    mSeekBar.setMax((int) totalSize);
                }
                mSeekBar.setProgress((int) bytesWritten);
                if (mSeekBar.getMax() > 0) {
                    int proNum = mSeekBar.getProgress() * 100 / mSeekBar.getMax();
                    tvCurrentTime.setText(proNum + "%");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                mProgressDialog.dismiss();
                ToastUtil.showToast(mContext, R.string.error_service);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                mVideo.setBackgroundColor(VideoPlayActivity.this.getResources().getColor(color.transparent));
                mVideo.setVideoPath(videoPath);
                mProgressDialog.dismiss();
                statuDownFinish();
                startPlay();
            }
        });
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

    @Override
    protected void onDestroy() {
        mProgressDialog.dismiss();
        ServiceProvider.cancelRequest(mContext);
        super.onDestroy();
    }

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
