package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.entity.category.Train;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.FullScreenVideoView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class TrainVideoActivity extends TrainBaseActivity {

    public static final String ENDWITH_MP4 = ".mp4"; // MP4后缀
    public static final int STATU_START_PLAY = 5; // 播放计时器
    private static final int FILE_SIZE = 9;

    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;

    // 自定义VideoView
    private FullScreenVideoView mVideoPlayer;
    private TextView mVideoTitleTextView; // 标题
    private ImageView mPlayerControlImageView; //开始/暂停 播放
    private TextView mFileSizeTextView; // 音乐文件大小
    private TextView mTotalTimeTextView;
    private TextView mCurrentTimeTextView;
    private ImageView mDownloadImageView;
    private ProgressBar mDownloadingProgressBar;
    private SeekBar mProgressSeekBar;
    private RelativeLayout mContainerLayout;
    private FrameLayout mPlayerContainerLayout;
    private LinearLayout mProgressContainerLayout;
    private LinearLayout mTopContainerLayout;

    private CheckBox mRotationCheckBox; // 屏幕旋转

    // 视频播放时间
    private int playTime;
    private String mSaveFilePath = ""; // 文件路径

    private ProgressHandler mProgressHandler;
    private boolean isChanging = false;// 互斥变量，防止定时器与SeekBar拖动时进度冲突

    public static Intent getIntent(Context context, Train train) {
        Intent intent = new Intent(context, TrainVideoActivity.class);
        intent.putExtra(KEY_TRAINING, train);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initView();
        initListener();
        initData();
    }

    /**
     * 功能描述: 布局初始化
     */
    protected void initView() {
        mPlayerControlImageView = (ImageView) findViewById(R.id.iv_activity_video_player_control);
        mVideoTitleTextView = (TextView) findViewById(R.id.tv_activity_video_player_title);
        mTotalTimeTextView = (TextView) findViewById(R.id.tv_activity_video_player_total_time);
        mCurrentTimeTextView = (TextView) findViewById(R.id.tv_activity_video_player_current_time);
        mDownloadImageView = (ImageView) findViewById(R.id.iv_activity_video_player_download);
        mFileSizeTextView = (TextView) findViewById(R.id.tv_activity_video_player_size);
        mDownloadingProgressBar = (ProgressBar) findViewById(R.id.pb_activity_video_player_downloading);
        mProgressSeekBar = (SeekBar) findViewById(R.id.sb_activity_video_player);
        mRotationCheckBox = (CheckBox) findViewById(R.id.cb_activity_video_player_zoom);
        mContainerLayout = (RelativeLayout) findViewById(R.id.rl_activity_video_player_container);
        mPlayerContainerLayout = (FrameLayout) findViewById(R.id.fl_activity_video_player_player_layout);
        mProgressContainerLayout = (LinearLayout) findViewById(R.id.ll_activity_video_player_progress_layout);
        mTopContainerLayout = (LinearLayout) findViewById(R.id.ll_activity_video_player_top);

        mVideoPlayer = (FullScreenVideoView) findViewById(R.id.fsvv_activity_video_player);
        mRotationCheckBox.setChecked(true);
    }

    /**
     * 功能描述: 监听初始化
     */
    private void initListener() {
        mDownloadImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.isNetConnected(mContext)) {
                    /** 开始下载 **/
                    statusDownloading();
                    startDownload();
                } else {
                    ToastUtil.showNetExc(mContext);
                }
            }
        });

        mPlayerControlImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoPlayer.isPlaying()) {
                    pausePlayer();
                } else {
                    startPlay();
                }
            }
        });

        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mVideoPlayer.seekTo(mProgressSeekBar.getProgress());
                isChanging = false;
            }
        });

        mRotationCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked) {
                    /** 竖屏 **/
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    // 显示控件
                    showVerticalView();
                } else {
                    /** 横屏 **/
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    // 隐藏控件
                    showHorizontalView();
                }
            }
        });

        // 在videoplayer上加点击事件无效，必须加在父控件上
        mPlayerContainerLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHide();
            }
        });
    }

    /**
     * 功能描述: 数据初始化
     */
    protected void initData() {
        mSaveFilePath = FileUtils.getTrainCacheDir(mContext) + mTrain.rid + ENDWITH_MP4;
        mVideoTitleTextView.setText(mTrain.subject);
        mProgressHandler = new ProgressHandler();

        File file = new File(mSaveFilePath);
        // 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
        if (file.exists()) {
            float fileSize = ((float) file.length()) / 1024f / 1024f;
            mFileSizeTextView.setText(String.format("视频文件（%.1fM）", fileSize));
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        URL DownRul = new URL(mTrain.url);
                        HttpURLConnection urlcon = (HttpURLConnection) DownRul.openConnection();
                        float fileSize = ((float) urlcon.getContentLength()) / 1024f / 1024f;
                        mProgressHandler.obtainMessage(FILE_SIZE, fileSize).sendToTarget();
                    } catch (Exception e) {
                        mProgressHandler.obtainMessage(FILE_SIZE, -1f).sendToTarget();
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        // 文件存在，下载完成
        if (file.exists()) {
            statusDownloadFinish();
            initVideo();
        } else {
            statusNotDownLoad();
            mTotalTimeTextView.setText("0%");
        }
    }

    /**
     * 没下载的状态 *
     */
    private void statusNotDownLoad() {
        mTotalTimeTextView.setText("0%");
        mDownloadImageView.setVisibility(View.VISIBLE);
        mPlayerControlImageView.setVisibility(View.GONE);
        mCurrentTimeTextView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.GONE);
        mRotationCheckBox.setVisibility(View.GONE);
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    /**
     * 下载完成,可以播放的状态 *
     */
    private void statusDownloadFinish() {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.VISIBLE);
        mCurrentTimeTextView.setVisibility(View.VISIBLE);
        mDownloadingProgressBar.setVisibility(View.GONE);
        mRotationCheckBox.setVisibility(View.VISIBLE);
        mProgressSeekBar.setEnabled(true);
        mProgressSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_));
    }

    /**
     * 下载中 *
     */
    private void statusDownloading() {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.GONE);
        mCurrentTimeTextView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.VISIBLE);
        mRotationCheckBox.setVisibility(View.GONE);
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    private void startDownload() {
        mDownloadImageView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.VISIBLE);
        ServiceProvider.doDownloadTrainingFile(mTrain.url, mSaveFilePath, new RangeFileAsyncHttpResponseHandler(new File(mSaveFilePath)) {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (mProgressSeekBar.getMax() != (int) totalSize)
                    mProgressSeekBar.setMax((int) totalSize);
                mProgressSeekBar.setProgress((int) bytesWritten);
                if (totalSize > 0 && bytesWritten > 0) {
                    mTotalTimeTextView.setText(100 * bytesWritten / totalSize + "%");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                ToastUtil.showNetExc(mContext);
                statusNotDownLoad();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                statusDownloadFinish();
                initVideo();
            }
        });
    }

    /**
     * 功能描述: 竖屏显示布局
     */
    private void showVerticalView() {
        mBottomView.setVisibility(View.VISIBLE);
        mTopContainerLayout.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        int height = getResources().getDimensionPixelSize(R.dimen.media_play_height);
        int screenWidth = DensityUtil.getWidthInPx(this);
        int marginLR = getResources().getDimensionPixelOffset(R.dimen.offset_lless);
        mContainerLayout.setPadding(marginLR, 0, marginLR, 0);

        mPlayerContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height));

        RelativeLayout.LayoutParams progressLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressLayout.addRule(RelativeLayout.BELOW, R.id.fl_activity_video_player_player_layout);
        mProgressContainerLayout.setLayoutParams(progressLayout);

        mVideoPlayer.setVideoWidth(screenWidth - 2 * marginLR);
        mVideoPlayer.setVideoHeight(height);
        mVideoPlayer.invalidate();
    }

    /**
     * 功能描述: 横屏隐藏布局
     */
    private void showHorizontalView() {
        mBottomView.setVisibility(View.GONE);
        mTopContainerLayout.setVisibility(View.GONE);
        getSupportActionBar().hide();
        int screenHeight = DensityUtil.getHeightInPx(mContext);
        int screenWidth = DensityUtil.getWidthInPx(mContext);
        mContainerLayout.setPadding(0, 0, 0, 0);

        mPlayerContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight));

        RelativeLayout.LayoutParams progressLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressLayout.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.fl_activity_video_player_player_layout);
        mProgressContainerLayout.setLayoutParams(progressLayout);

        mVideoPlayer.setVideoWidth(screenWidth);
        mVideoPlayer.setVideoHeight(screenHeight);
        mVideoPlayer.invalidate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 屏幕大小改变监听
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showHorizontalView(); // 竖屏
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            showVerticalView(); // 横屏
        }
        super.onConfigurationChanged(newConfig);
    }

    class ProgressHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATU_START_PLAY:
                    if (!mActivity.isFinishing() && mVideoPlayer != null && mVideoPlayer.isPlaying()) {
                        if (!isChanging) {
                            String currentTime = new SimpleDateFormat("mm:ss").format(mVideoPlayer
                                    .getCurrentPosition());
                            mCurrentTimeTextView.setText(currentTime);
                            mProgressSeekBar.setProgress(mVideoPlayer.getCurrentPosition());
                        }

                        mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
                    }
                    break;
                case FILE_SIZE:
                    float fileSize = (float) msg.obj;
                    if (fileSize > 0) {
                        mFileSizeTextView.setText(String.format("视频文件（%.1fM）", fileSize));
                    } else {
                        ToastUtil.showToast(mContext, R.string.tips_audio_get_size_fail);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 功能描述:初始化播放器
     */
    private void initVideo() {

        mCurrentTimeTextView.setText("00:00");
        mProgressSeekBar.setProgress(0);
        mVideoPlayer.setVideoPath(mSaveFilePath);
        mVideoPlayer.requestFocus();
        mVideoPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 设置大小
                mVideoPlayer.setVideoWidth(mp.getVideoWidth());
                mVideoPlayer.setVideoHeight(mp.getVideoHeight());
                mProgressSeekBar.setMax(mVideoPlayer.getDuration());
                mTotalTimeTextView.setText(new SimpleDateFormat("mm:ss").format(mVideoPlayer.getDuration()));
                if (playTime != 0) {
                    mCurrentTimeTextView.setText(new SimpleDateFormat("mm:ss").format(playTime));
                    mProgressSeekBar.setProgress(playTime);
                    mVideoPlayer.seekTo(playTime);
                }
            }
        });

        mVideoPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
                mCurrentTimeTextView.setText("00:00");
                mProgressSeekBar.setProgress(0);
            }
        });
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                showOrHide();
            }

        }
    };

    /**
     * 功能描述: 视屏开始播放
     */
    private void startPlay() {
        if (!mVideoPlayer.isPlaying()) {
            mVideoPlayer.start();
            mVideoPlayer.setBackgroundColor(0x00000000);
            mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
            mPlayerControlImageView.setImageResource(R.drawable.button_media_stop);

            // 一段时间后隐藏底端布局
            mProgressHandler.removeCallbacks(hideRunnable);
            mProgressHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    private void pausePlayer() {
        if (mVideoPlayer.isPlaying()) {
            mVideoPlayer.pause();
            mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
        }
    }

    private void showOrHide() {
        if (mProgressContainerLayout.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                }
            });

            mProgressContainerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mProgressContainerLayout.setVisibility(View.GONE);
                }
            });
            mProgressContainerLayout.startAnimation(animation1);
            mProgressHandler.removeCallbacks(hideRunnable);
        } else {
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_top);
            mProgressContainerLayout.setVisibility(View.VISIBLE);
            mProgressContainerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_bottom);
            mProgressContainerLayout.startAnimation(animation1);
            mProgressHandler.removeCallbacks(hideRunnable);
            mProgressHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    private class AnimationImp implements AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
            pausePlayer();
        }
    }

    // 来电处理
    @Override
    protected void onDestroy() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isPlaying()) {
                mVideoPlayer.pause();
            }
            mVideoPlayer = null;
        }
        ServiceProvider.cancelRequest();
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        playTime = state.getInt("position");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mVideoPlayer.getCurrentPosition());
    }
}
