package com.badou.mworking.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.TrainBaseActivity;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.presenter.DownloadPresenter;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.view.DownloadView;
import com.badou.mworking.widget.FullScreenVideoView;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrainVideoFragment extends BaseFragment implements DownloadView {

    static final String KEY_RID = "rid";
    static final String KEY_URL = "url";
    static final String KEY_SUBJECT = "subject";

    @Bind(R.id.video_title_text_view)
    TextView mVideoTitleTextView;
    @Bind(R.id.file_size_text_view)
    TextView mFileSizeTextView;
    @Bind(R.id.top_container_layout)
    LinearLayout mTopContainerLayout;
    @Bind(R.id.video_player)
    FullScreenVideoView mVideoPlayer;
    @Bind(R.id.download_image_view)
    ImageView mDownloadImageView;
    @Bind(R.id.downloading_progress_bar)
    ProgressBar mDownloadingProgressBar;
    @Bind(R.id.player_container_layout)
    FrameLayout mPlayerContainerLayout;
    @Bind(R.id.player_control_image_view)
    ImageView mPlayerControlImageView;
    @Bind(R.id.current_time_text_view)
    TextView mCurrentTimeTextView;
    @Bind(R.id.progress_seek_bar)
    SeekBar mProgressSeekBar;
    @Bind(R.id.total_time_text_view)
    TextView mTotalTimeTextView;
    @Bind(R.id.rotation_check_box)
    CheckBox mRotationCheckBox;
    @Bind(R.id.progress_container_layout)
    LinearLayout mProgressContainerLayout;
    @Bind(R.id.container_layout)
    RelativeLayout mContainerLayout;

    private DownloadPresenter mPresenter;
    private int lastTime = 0;

    public static TrainVideoFragment getFragment(String rid, String url, String subject) {
        TrainVideoFragment trainVideoFragment = new TrainVideoFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_RID, rid);
        argument.putString(KEY_URL, url);
        argument.putString(KEY_SUBJECT, subject);
        trainVideoFragment.setArguments(argument);
        return trainVideoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_video_player, container, false);
        ButterKnife.bind(this, view);
        Bundle argument = getArguments();
        mVideoTitleTextView.setText(argument.getString(KEY_SUBJECT));
        mPresenter = new DownloadPresenter(mContext, argument.getString(KEY_RID), argument.getString(KEY_URL), Constant.MWKG_FORAMT_TYPE_MP3);
        initView(argument);
        mPresenter.attachView(this);
        return view;
    }

    private void initView(Bundle argument) {
        mRotationCheckBox.setChecked(isVertical());
        initListener();
        // 每一次改变方向都会重新调用onCreate，因此需要判断
        if (isVertical()) {
            showVerticalView();
        } else {
            showHorizontalView();
        }
        if (argument != null && argument.containsKey("position")) {
            lastTime = argument.getInt("position");
        } else {
            lastTime = -1;
        }
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
    void onDownloadClicked() {
        mPresenter.startDownload();
    }

    @OnClick(R.id.player_control_image_view)
    void onControlClicked() {
        mPresenter.statusChange(mVideoPlayer.isPlaying());
    }

    @OnClick(R.id.rotation_check_box)
    void onRotationClicked() {
        Bundle argument = getArguments();
        argument.putInt("position", mVideoPlayer.getCurrentPosition());
        if (!isVertical()) {
            mRotationCheckBox.setChecked(true);
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //mPresenter.onRotationChanged(false);
        } else {
            mRotationCheckBox.setChecked(false);
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //mPresenter.onRotationChanged(true);
        }
    }

    @OnClick(R.id.player_container_layout)
    void onPlayerLayoutClicked() {
        setProgressBar(mProgressContainerLayout.getVisibility() == View.GONE);
    }

    /**
     * 没下载的状态 *
     */
    public void statusNotDownLoad() {
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
    @Override
    public void statusDownloadFinish(File file) {
        mDownloadImageView.setVisibility(View.GONE);
        mPlayerControlImageView.setVisibility(View.VISIBLE);
        mCurrentTimeTextView.setVisibility(View.VISIBLE);
        mDownloadingProgressBar.setVisibility(View.GONE);
        mRotationCheckBox.setVisibility(View.VISIBLE);
        mProgressSeekBar.setEnabled(true);
        mProgressSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_));
        float fileSize = ((float) file.length()) / 1024f / 1024f;
        setFileSize(fileSize);
        initVideo(file);
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
        mRotationCheckBox.setVisibility(View.GONE);
        mProgressSeekBar.setEnabled(false);// 下载中禁止用户拖动
        mProgressSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
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

    /**
     * 功能描述: 竖屏显示布局
     */
    public void showVerticalView() {
        ((TrainBaseActivity) mActivity).setBottomViewVisible(true);
        mTopContainerLayout.setVisibility(View.VISIBLE);
        ((BaseActionBarActivity) mActivity).showActionbar();
        int height = getResources().getDimensionPixelSize(R.dimen.media_play_height);
        int screenWidth = DensityUtil.getWidthInPx(mActivity);
        int marginLR = getResources().getDimensionPixelOffset(R.dimen.offset_lless);
        mContainerLayout.setPadding(marginLR, 0, marginLR, 0);

        mPlayerContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, height));

        RelativeLayout.LayoutParams progressLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressLayout.addRule(RelativeLayout.BELOW, R.id.player_container_layout);
        mProgressContainerLayout.setLayoutParams(progressLayout);

        mVideoPlayer.setVideoWidth(screenWidth - 2 * marginLR);
        mVideoPlayer.setVideoHeight(height);
        mVideoPlayer.invalidate();
    }

    /**
     * 功能描述: 横屏隐藏布局
     */
    public void showHorizontalView() {
        ((TrainBaseActivity) mActivity).setBottomViewVisible(false);
        mTopContainerLayout.setVisibility(View.GONE);
        ((BaseActionBarActivity) mActivity).hideActionbar();
        int screenHeight = DensityUtil.getInstance().getScreenHeight();
        int screenWidth = DensityUtil.getInstance().getScreenWidth();
        mContainerLayout.setPadding(0, 0, 0, 0);

        mPlayerContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight));

        RelativeLayout.LayoutParams progressLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressLayout.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.player_container_layout);
        mProgressContainerLayout.setLayoutParams(progressLayout);

        mVideoPlayer.setVideoWidth(screenWidth);
        mVideoPlayer.setVideoHeight(screenHeight);
        mVideoPlayer.invalidate();
    }

    @Override
    public void setPlayTime(int time, boolean isVideo) {
        if (isVideo)
            mVideoPlayer.seekTo(time);
        mProgressSeekBar.setProgress(time);
        mCurrentTimeTextView.setText(new SimpleDateFormat("mm:ss").format(time));
    }

    @Override
    public void setFileSize(float fileSize) {
        if (mFileSizeTextView != null)
            mFileSizeTextView.setText(String.format("视频文件（%.1fM）", fileSize));
    }

    @Override
    public void setProgressBar(boolean visible) {
        if (!visible && mProgressContainerLayout.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                }
            });

            mProgressContainerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mProgressContainerLayout.setVisibility(View.GONE);
                }
            });
            mProgressContainerLayout.startAnimation(animation1);
        } else if (visible && mProgressContainerLayout.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.option_entry_from_top);
            mProgressContainerLayout.setVisibility(View.VISIBLE);
            mProgressContainerLayout.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.option_entry_from_bottom);
            mProgressContainerLayout.startAnimation(animation1);
        }
    }

    /**
     * 功能描述:初始化播放器
     */
    private void initVideo(File file) {
        mCurrentTimeTextView.setText("00:00");
        mProgressSeekBar.setProgress(0);
        try {
            mVideoPlayer.setVideoURI(Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
            mPresenter.fileCrashed();
            return;
        }
        mVideoPlayer.requestFocus();
        mVideoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 设置大小
                mVideoPlayer.setVideoWidth(mp.getVideoWidth());
                mVideoPlayer.setVideoHeight(mp.getVideoHeight());
                mProgressSeekBar.setMax(mVideoPlayer.getDuration());
                mTotalTimeTextView.setText(new SimpleDateFormat("mm:ss").format(mVideoPlayer.getDuration()));
                if (lastTime > 0)
                    setPlayTime(lastTime, true);
            }
        });

        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
                mCurrentTimeTextView.setText("00:00");
                mProgressSeekBar.setProgress(0);
            }
        });
    }

    /**
     * 功能描述: 视屏开始播放
     */
    public void startPlay() {
        if (mVideoPlayer != null && !mVideoPlayer.isPlaying()) {
            mVideoPlayer.start();
            mVideoPlayer.setBackgroundColor(0x00000000);
            mPlayerControlImageView.setImageResource(R.drawable.button_media_stop);
        }
    }

    @Override
    public void stopPlay() {
        if (mVideoPlayer != null && mVideoPlayer.isPlaying()) {
            mVideoPlayer.pause();
            mPlayerControlImageView.setImageResource(R.drawable.button_media_start);
        }
    }

    @Override
    public int getCurrentTime() {
        return mVideoPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return mVideoPlayer.isPlaying();
    }

    @Override
    public boolean isVertical() {
        return mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private class AnimationImp implements Animation.AnimationListener {

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
    public void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
