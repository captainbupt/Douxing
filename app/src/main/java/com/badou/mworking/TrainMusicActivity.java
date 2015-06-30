package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.entity.category.Train;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class TrainMusicActivity extends TrainBaseActivity {

    public static final int STATU_START_PLAY = 5; // 播放计时器
    private static final int FILE_SIZE = 6;
    public static final String ENDWITH_MP3 = ".mp3"; // MP3后缀名

    private TextView mMusicTitleTextView; // 标题
    private ImageView mPlayerControlImageView; //开始/暂停 播放
    private TextView mFileSizeTextView; // 音乐文件大小
    private TextView mTotalTimeTextView;
    private TextView mCurrentTimeTextView;
    private ImageView mDownloadImageView;
    private ProgressBar mDownloadingProgressBar;
    private SeekBar mProgressSeekBar;

    private MediaPlayer mMusicPlayer = null;

    private boolean isChanging = false;// 互斥变量，防止定时器与SeekBar拖动时进度冲突
    private ProgressHandler mProgressHandler; // 刷新进度条

    private HttpHandler mDownloadingHandler; // 下载

    private String mSaveFilePath; // 保存文件的路径,无文件名

    public static Intent getIntent(Context context, Train train) {
        Intent intent = new Intent(context, TrainMusicActivity.class);
        intent.putExtra(KEY_TRAINING, train);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        initView();// 各组件
        initListener();
        initData();
    }

    /**
     * 功能描述:初始化控件和路径
     */
    protected void initView() {
        mPlayerControlImageView = (ImageView) findViewById(R.id.iv_activity_music_player_control);
        mMusicTitleTextView = (TextView) findViewById(R.id.tv_activity_music_player_title);
        mTotalTimeTextView = (TextView) findViewById(R.id.tv_activity_music_player_total_time);
        mCurrentTimeTextView = (TextView) findViewById(R.id.tv_activity_music_player_current_time);
        mDownloadImageView = (ImageView) findViewById(R.id.iv_activity_music_player_download);
        mFileSizeTextView = (TextView) findViewById(R.id.tv_activity_music_player_size);
        mDownloadingProgressBar = (ProgressBar) findViewById(R.id.pb_activity_music_player_downloading);
        mProgressSeekBar = (SeekBar) findViewById(R.id.sb_activity_music_player);
    }

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
                mMusicPlayer.seekTo(mProgressSeekBar.getProgress());
                isChanging = false;
            }
        });
        mPlayerControlImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断有没有要播放的文件
                if (new File(mSaveFilePath).exists() && mMusicPlayer != null) {
                    if (!mMusicPlayer.isPlaying()) {
                        startPlay();
                    } else {
                        pausePlay();
                    }
                }
            }
        });
    }

    private void initData() {
        mMusicTitleTextView.setText(mTrain.subject);
        mMusicPlayer = new MediaPlayer();
        mSaveFilePath = FileUtils.getTrainCacheDir(mContext) + mTrain.rid + ENDWITH_MP3;

        mProgressHandler = new ProgressHandler();
        File file = new File(mSaveFilePath);

        // 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
        if (file.exists()) {
            float fileSize = ((float) file.length()) / 1024f / 1024f;
            mFileSizeTextView.setText(String.format("音频文件（%.1fM）", fileSize));
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        URL DownRul = new URL(mTrain.url);
                        HttpURLConnection urlcon = (HttpURLConnection) DownRul.openConnection();
                        float fileSize = ((float) urlcon.getContentLength()) / 1024 / 1024;
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
            statuDownloadFinish();
            initMedia();
        } else {
            statusNotDownLoad();
        }
    }

    /**
     * 功能描述:初始化播放器
     */
    private void initMedia() {
        if (mMusicPlayer == null) {
            return;
        }
        try {
            mMusicPlayer.reset();
            mMusicPlayer.setDataSource(mSaveFilePath);
            mMusicPlayer.prepare();// 就是把存储卡中的内容全部加载或者网络中的部分媒体内容加载到内存中，有可能会失败抛出异常的
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(mContext, R.string.tips_audio_error);
            new File(mSaveFilePath).delete();
            statusNotDownLoad();
            return;
        }
        mProgressSeekBar.setMax(mMusicPlayer.getDuration());// 设置进度条
        mProgressSeekBar.setProgress(0);
        mTotalTimeTextView.setText(new SimpleDateFormat("mm:ss").format(mMusicPlayer.getDuration()));
        mCurrentTimeTextView.setText("00:00");
    }

    /**
     * 功能描述: 开始播放
     */
    private void startPlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_stop);
        mMusicPlayer.start();// 开始
        mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
    }

    private void pausePlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
        mMusicPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            pausePlay();
        }
    }

    // 来电处理
    @Override
    protected void onDestroy() {
        if (mMusicPlayer != null) {
            if (mMusicPlayer.isPlaying()) {
                mMusicPlayer.stop();
            }
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
        if (mDownloadingHandler != null)
            mDownloadingHandler.cancel();
        super.onDestroy();
    }

    class ProgressHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATU_START_PLAY:
                    if (!mActivity.isFinishing() && mMusicPlayer != null && mMusicPlayer.isPlaying()) {
                        if (!isChanging) {
                            String currentTime = new SimpleDateFormat("mm:ss").format(mMusicPlayer
                                    .getCurrentPosition());
                            mCurrentTimeTextView.setText(currentTime);
                            mProgressSeekBar.setProgress(mMusicPlayer.getCurrentPosition());
                        }
                        mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
                    }
                    break;
                case FILE_SIZE:
                    float fileSize = (float) msg.obj;
                    if (fileSize > 0) {
                        mFileSizeTextView.setText(String.format("音频文件（%.1fM）", fileSize));
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
     * 没下载的状态 *
     */
    private void statusNotDownLoad() {
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
    private void statuDownloadFinish() {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.VISIBLE);
        mCurrentTimeTextView.setVisibility(View.VISIBLE);
        mDownloadingProgressBar.setVisibility(View.GONE);
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
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
    }

    private void startDownload() {
        mDownloadImageView.setVisibility(View.GONE);
        mDownloadingProgressBar.setVisibility(View.VISIBLE);
        if (mDownloadingHandler == null) {
            HttpUtils http = new HttpUtils();
            mDownloadingHandler = http.download(mTrain.url,
                    mSaveFilePath + ".tmp",
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            if (mProgressSeekBar.getMax() != total)
                                mProgressSeekBar.setMax((int) total);
                            mProgressSeekBar.setProgress((int) current);
                            if (total > 0 && current > 0) {
                                mTotalTimeTextView.setText(100 * current / total + "%");
                            }
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), mTrain.rid + ENDWITH_MP3 + ".tmp", mTrain.rid + ENDWITH_MP3);
                            statuDownloadFinish();
                            initMedia();
                            startPlay();
                        }


                        @Override
                        public void onFailure(HttpException error, String msg) {
                            new File(mSaveFilePath + ".tmp").delete();
                        }
                    });
        }
    }
}
