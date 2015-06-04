package com.badou.mworking;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomRatingAndCommentView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.TextView;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class TrainMusicActivity extends BaseBackActionBarActivity {

    public static final String KEY_RID = "rid";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_URL = "url";

    private TextView mMusicTitleTextView; // 标题
    private ImageView mPlayerControlImageView; //开始/暂停 播放
    private TextView mFileSizeTextView; // 音乐文件大小
    private TextView mTotalTimeTextView;
    private TextView mCurrentTimeTextView;
    private ImageView mDownloadImageView;
    private ProgressBar mDownloadingProgressBar;
    private SeekBar mProgressSeekBar;
    private BottomRatingAndCommentView mBottomLayout;

    /**
     * 媒体播放 *
     */
    private MediaPlayer player = null;

    private boolean isChanging = false;// 互斥变量，防止定时器与SeekBar拖动时进度冲突
    /**
     * 主线程 *
     */
    private MyHadler mProgressHandler;
    private HttpHandler mDownloadingHandler;

    /**
     * 保存文件的路径,无文件名 *
     */
    private String mSaveFilePath;

    public static final int STATU_START_PLAY = 5; // 播放计时器
    private static final int FILE_SIZE = 6;

    /**
     * MP3后缀名 *
     */
    public static final String ENDWITH_MP3 = ".mp3";

    private String mUrl = "";
    private String mRid = "";

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
        mBottomLayout = (BottomRatingAndCommentView) findViewById(R.id.bracv_activity_music_player);
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
                player.seekTo(mProgressSeekBar.getProgress());
                isChanging = false;
            }
        });
        mPlayerControlImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断有没有要播放的文件
                if (new File(mSaveFilePath).exists() && player != null) {
                    if (!player.isPlaying()) {
                        startPlay();
                    } else {
                        pausePlay();
                    }
                }
            }
        });
    }

    private void initData() {
        mRid = mReceivedIntent.getStringExtra(KEY_RID);
        mUrl = mReceivedIntent.getStringExtra(KEY_URL);
        mMusicTitleTextView.setText(mReceivedIntent.getStringExtra(KEY_SUBJECT));
        player = new MediaPlayer();
        mSaveFilePath = FileUtils.getTrainCacheDir(mContext) + mRid + ENDWITH_MP3;
        File file = new File(mSaveFilePath);

        // 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
        if (file.exists()) {
            float fileSize = (float) (Math.round(file.length() / 1024 / 1024 * 10)) / 10;
            mFileSizeTextView.setText(fileSize + "M");
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        URL DownRul = new URL(mUrl);
                        HttpURLConnection urlcon = (HttpURLConnection) DownRul.openConnection();
                        float fileSize = (float) (Math.round(urlcon.getContentLength() / 1024 / 1024 * 10)) / 10;
                        mProgressHandler.obtainMessage(FILE_SIZE, fileSize).sendToTarget();
                    } catch (Exception e) {
                        ToastUtil.showToast(mContext, R.string.tips_audio_get_size_fail);
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        // 这里加个保护，就是在下载中，退出界面，广播也关了，下载完之后没有重命名
        String fileNames[] = mUrl.split("=");
        FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), fileNames[fileNames.length - 1], mRid + ".mp3");
        // 文件存在，下载完成
        if (file.exists()) {
            statuDownloadFinish();
        } else {
            statuNotDownLoad();
        }
        if (((AppApplication) getApplication()).getUserInfo().isAdmin) {
            setRightImage(R.drawable.button_title_admin_statistical);
        }
        mBottomLayout.setData(mRid, 0, 0, -1);

    }

    /**
     * 功能描述:初始化播放器
     */
    private void initMedia() {
        if (player == null) {
            return;
        }
        try {
            player.reset();
            player.setDataSource(mSaveFilePath);
            player.prepare();// 就是把存储卡中的内容全部加载或者网络中的部分媒体内容加载到内存中，有可能会失败抛出异常的
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(mContext, R.string.tips_audio_error);
            new File(mSaveFilePath).delete();
            statuNotDownLoad();
            return;
        }
        mProgressSeekBar.setMax(player.getDuration());// 设置进度条
        mProgressSeekBar.setProgress(0);
        mTotalTimeTextView.setText(new SimpleDateFormat("mm:ss").format(player.getDuration()));
        mCurrentTimeTextView.setText("00:00");
    }

    /**
     * 功能描述: 开始播放
     */
    private void startPlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_stop);
        player.start();// 开始
        if (mProgressHandler == null)
            mProgressHandler = new MyHadler();
        mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
    }

    private void pausePlay() {
        mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
        player.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) {
            pausePlay();
        }
    }

    // 来电处理
    @Override
    protected void onDestroy() {
        releasePlayer();
        if (mDownloadingHandler != null)
            mDownloadingHandler.cancel();
        super.onDestroy();
    }

    private void releasePlayer() {
        // 确定按钮
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    class MyHadler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATU_START_PLAY:
                    if (!mActivity.isFinishing() && player != null && player.isPlaying()) {
                        String currentTime = new SimpleDateFormat("mm:ss").format(player
                                .getCurrentPosition());
                        mCurrentTimeTextView.setText(currentTime);
                        mProgressSeekBar.setProgress(player.getCurrentPosition());
                        mProgressHandler.sendEmptyMessageDelayed(STATU_START_PLAY, 1000);
                    }
                    break;
                case FILE_SIZE:
                    mFileSizeTextView.setText("音频文件(" + msg.obj + "M)");
                    break;
                default:
                    break;
            }
        }
    }

    public void clickRight() {
        //  跳转到评论页面
        Intent intent = new Intent(mContext, CommentActivity.class);
        intent.putExtra(CommentActivity.KEY_RID, mRid);
        startActivity(intent);
    }

    /**
     * 没下载的状态 *
     */
    private void statuNotDownLoad() {
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
        /** 下载完成,初始化播放器 **/
        initMedia();
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
        if (mDownloadingHandler == null) {
            HttpUtils http = new HttpUtils();
            mDownloadingHandler = http.download(mUrl,
                    mSaveFilePath + ".tmp",
                    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            if (mProgressSeekBar.getMax() != total)
                                mProgressSeekBar.setMax((int) total);
                            mDownloadImageView.setVisibility(View.GONE);
                            mDownloadingProgressBar.setVisibility(View.VISIBLE);
                            mProgressSeekBar.setProgress((int) current);
                            mTotalTimeTextView.setText(100 % current / total + "%");
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), mRid + ENDWITH_MP3 + ".tmp", mRid + ENDWITH_MP3);
                            statuDownloadFinish();
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
