package com.badou.mworking;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Train;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.MyIntents;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.FullScreenVideoView;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TrainVideoPlayerAct extends TrainBaseActivity implements
		OnClickListener {

	/** 实体类 **/
	private Train train;
	// 自定义VideoView
	private FullScreenVideoView mVideo;
	// 底部View
	private View mBottomView;
	// 视频播放拖动条
	public SeekBar mSeekBar;

	private TextView tvPlayer; // 控制播放的按钮
	private TextView currentTimeTv; // 显示当前的时间
	private TextView allTimeTv; // 总时长
	private TextView tvTop; // 标题
	private TextView musicFileSizeTv; // 视屏文件大小
	private TextView wifiTips; // 建议在wifi环境下观看

	/** 下载时显示的进度条 **/
	private ProgressBar mProgressBar;
	/** 开始下载的按钮 **/
	private ImageView imgStartDown;
	/** 控制全屏 **/
	private CheckBox rotationTv; // 屏幕旋转

	private RelativeLayout layoutActionBg; // actionbar 布局
	private RelativeLayout screenView; // 视屏播放区域
	private LinearLayout weipeixuncommentRelat; // 培训底部布局

	/** 手机屏幕宽高 **/
	private int screenWidth;
	private int screenHeight;

	private int mraginLR;

	// 视频播放时间
	private int playTime;
	/** 路径 **/
	private String fileDir = "";
	/** 媒体文件 **/
	private File fileMedia;
	/** mp4的后缀 **/
	public static final String ENDWITH_MP4 = ".mp4";

	private static final int FILE_SIZE = 9;

	private String filesize;

	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TIME = 5000;
	
    private MyReceiver mReceiver;
    
    private String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		initView();
		initData();
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		imgStartDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetUtils.isNetConnected(mContext)) {
					statuDownloading();
					// 开启下载服务，进行的是添加操作
	                Intent downloadIntent = new Intent("com.badou.mworking.services.IDownloadService");
	                downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
	                downloadIntent.putExtra(MyIntents.URL, url);
	                startService(downloadIntent);
				} else {
					ToastUtil.showNetExc(mContext);
				}
			}
		});

		rotationTv.setChecked(true);
		rotationTv.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					/** 竖屏 **/
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					// 显示控件
					showView();
				} else {
					/** 横屏 **/
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					// 隐藏控件
					hideView();
				}
			}
		});
	        
        //开启下载服务，进行的是开始下载操作
        Intent downloadIntent = new Intent("com.badou.mworking.services.IDownloadService");
        downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
        startService(downloadIntent);

        //广播，接收
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.badou.mworking.TrainVideoPlayerAct");
        registerReceiver(mReceiver, filter);
        
     // 这里加个保护，就是在下载中，退出界面，广播也关了，下载完之后没有重命名
 		String fileNames[] = url.split("=");
 		FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), fileNames[fileNames.length-1], train.getRid() + ENDWITH_MP4);
 		// 文件存在，下载完成
 		if (fileMedia.exists()) {
 			statuDownFinish();
 		} else {
 			statuNotDown();
 			allTimeTv.setText("0%");
 		}
		
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 功能描述: 布局初始化
	 */
	protected void initView() {
		super.initView();
		rotationTv = (CheckBox) findViewById(R.id.chkZoom);
		mVideo = (FullScreenVideoView) findViewById(R.id.videoview);
		currentTimeTv = (TextView) findViewById(R.id.tvCurrentTime);
		allTimeTv = (TextView) findViewById(R.id.tvTotalTime);
		tvPlayer = (TextView) findViewById(R.id.tvPlayer);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mBottomView = findViewById(R.id.bottom_layout);
		mProgressBar = (ProgressBar) findViewById(R.id.pro_downloading);
		imgStartDown = (ImageView) findViewById(R.id.img);
		layoutActionBg = (RelativeLayout) findViewById(R.id.layout_action);
		screenView = (RelativeLayout) findViewById(R.id.screen_view);
		weipeixuncommentRelat = (LinearLayout) findViewById(R.id.layout_bottom);
		tvTop = (TextView) findViewById(R.id.tvTop);
		musicFileSizeTv = (TextView) findViewById(R.id.tvTips);
		wifiTips = (TextView) findViewById(R.id.wifi_tips);
		screenWidth = DensityUtil.getWidthInPx(this);
		screenHeight = DensityUtil.getHeightInPx(this);
		threshold = DensityUtil.dip2px(this, 18);
		LayoutParams screenLR = (LayoutParams) screenView.getLayoutParams();
		mraginLR = screenLR.leftMargin;
		tvPlayer.setOnClickListener(this);

		fileDir = FileUtils.getTrainCacheDir(mContext) + train.getRid() + ENDWITH_MP4;
		fileMedia = new File(fileDir);
	}

	/**
	 * 功能描述: 数据初始化
	 */
	protected void initData() {
		try {
			train = (Train) getIntent().getBundleExtra("train")
					.getSerializable("train");
			String title = train.getSubject();
			if (!TextUtils.isEmpty(title)) {
				tvTop.setText(title);
			}

			url = train.getUrl()
					+ "&uid="
					+ ((AppApplication) mContext.getApplicationContext())
							.getUserInfo().getUserId();

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if(fileMedia.exists()){
							float fileLength = fileMedia.length();	
							filesize = (float) (Math
									.round(fileLength / 1024 / 1024 * 10))
									/ 10
									+ "M";
						}else{
							URL DownRul = new URL(url);
							HttpURLConnection urlcon = (HttpURLConnection) DownRul
									.openConnection();
							float fileLength = urlcon.getContentLength();	
							filesize = (float) (Math
									.round(fileLength / 1024 / 1024 * 10))
									/ 10
									+ "M";
						}
						mHandler.sendEmptyMessage(FILE_SIZE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述: 竖屏显示布局
	 */
	private void showView() {
		layoutActionBg.setVisibility(View.VISIBLE);
		weipeixuncommentRelat.setVisibility(View.VISIBLE);
		tvTop.setVisibility(View.VISIBLE);
		musicFileSizeTv.setVisibility(View.VISIBLE);
		wifiTips.setVisibility(View.VISIBLE);
	}

	/**
	 * 功能描述: 横屏隐藏布局
	 */
	private void hideView() {
		layoutActionBg.setVisibility(View.GONE);
		weipeixuncommentRelat.setVisibility(View.GONE);
		tvTop.setVisibility(View.GONE);
		musicFileSizeTv.setVisibility(View.GONE);
		wifiTips.setVisibility(View.GONE);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		/***
		 *  屏幕大小改变监听
		 */
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setVideoHeng();
			// ToastUtil.showToast(mContext,"screenWidth == " +screenWidth +
			// " **** 横屏 ");
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setVideoShu();
			// ToastUtil.showToast(mContext,"screenWidth == " +screenWidth +
			// " **** 竖屏 ");
		}
		super.onConfigurationChanged(newConfig);
	}

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mHandler.removeCallbacks(hideRunnable);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				int time = progress * mVideo.getDuration() / 100;
				mVideo.seekTo(time);
			}
		}
	};

	private void backward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int backwardTime = (int) (delataX / screenWidth * mVideo.getDuration());
		int currentTime = current - backwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		currentTimeTv.setText(formatTime(currentTime));
	}

	private void forward(float delataX) {
		int current = mVideo.getCurrentPosition();
		int forwardTime = (int) (delataX / screenWidth * mVideo.getDuration());
		int currentTime = current + forwardTime;
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
		currentTimeTv.setText(formatTime(currentTime));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mHandler.removeMessages(0);
		mHandler.removeCallbacksAndMessages(null);
	}
	
	
	
	Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TrainMusicActivity.STATU_START_PLAY:
				if (mVideo.isPlaying()) {
					if (mVideo.getCurrentPosition() > 0) {
						currentTimeTv.setText(formatTime(mVideo
								.getCurrentPosition()));
						int progress = mVideo.getCurrentPosition() * 100
								/ mVideo.getDuration();
						mSeekBar.setProgress(progress);
						if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
							currentTimeTv.setText("00:00");
							mSeekBar.setProgress(0);
						}
						mSeekBar.setSecondaryProgress(mVideo
								.getBufferPercentage());
					} else {
						currentTimeTv.setText("00:00");
						mSeekBar.setProgress(0);
					}
				}
				break;
			/*case TrainActivity.PROGRESS_FINISH:
				statuDownFinish();
				startPlay();
				break;*/
			case FILE_SIZE:
				musicFileSizeTv.setText("视频文件(" + filesize + ")");
				break;
			default:
				break;
			}
		}
	};
	
	/***
	 * 
	 * 功能描述:初始化播放器
	 */
	private void playVideo() {
		currentTimeTv.setText("00:00");
		mSeekBar.setProgress(0);
		mVideo.setVideoPath(fileMedia.getAbsolutePath());
		mVideo.requestFocus();
		mVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				//  设置大小
				mVideo.setVideoWidth(mp.getVideoWidth());
				mVideo.setVideoHeight(mp.getVideoHeight());
				//  开始播放
				// mVideo.start();
				if (playTime != 0) {
					mVideo.seekTo(playTime);
				}

				mHandler.removeCallbacks(hideRunnable);
				mHandler.postDelayed(hideRunnable, HIDE_TIME);
				allTimeTv.setText(formatTime(mVideo.getDuration()));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						//  更新播放时间
						mHandler.sendEmptyMessage(TrainMusicActivity.STATU_START_PLAY);
					}
				}, 0, 1000);
			}
		});

		mVideo.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				tvPlayer.setBackgroundResource(R.drawable.btn_start);
				currentTimeTv.setText("00:00");
				mSeekBar.setProgress(0);
			}
		});
		mVideo.setOnTouchListener(mTouchListener);
	}

	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				showOrHide();
			}

		}
	};

	@SuppressLint("SimpleDateFormat")
	private String formatTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private float mLastMotionX;
	private float mLastMotionY;
	private int startX;
	private int startY;
	private int threshold;
	private boolean isClick = true;

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final float x = event.getX();
			final float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mLastMotionY = y;
				startX = (int) x;
				startY = (int) y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = x - mLastMotionX;
				float deltaY = y - mLastMotionY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);
				// 声音调节标识
				boolean isAdjustAudio = false;
				if (absDeltaX > threshold && absDeltaY > threshold) {
					if (absDeltaX < absDeltaY) {
						isAdjustAudio = true;
					} else {
						isAdjustAudio = false;
					}
				} else if (absDeltaX < threshold && absDeltaY > threshold) {
					isAdjustAudio = true;
				} else if (absDeltaX > threshold && absDeltaY < threshold) {
					isAdjustAudio = false;
				} else {
					return true;
				}
				if (!isAdjustAudio) {
					if (deltaX > 0) {
						forward(absDeltaX);
					} else if (deltaX < 0) {
						backward(absDeltaX);
					}
				}
				mLastMotionX = x;
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_UP:
				if (Math.abs(x - startX) > threshold
						|| Math.abs(y - startY) > threshold) {
					isClick = false;
				}
				mLastMotionX = 0;
				mLastMotionY = 0;
				startX = (int) 0;
				if (isClick) {
					showOrHide();
				}
				isClick = true;
				break;

			default:
				break;
			}
			return true;
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvPlayer:
			startPlay();
			break;
		case R.id.iv_actionbar_left:
			TrainVideoPlayerAct.this.finish();
			break;
		default:
			break;
		}
	}

	
	/**
	 * 功能描述: 视屏开始播放
	 */
	@SuppressLint("NewApi")
	private void startPlay(){
		if (mVideo.isPlaying()) {
			mVideo.pause();
			tvPlayer.setBackgroundResource(R.drawable.btn_start);
		} else {
			mVideo.setBackground(null);
			mVideo.start();
			tvPlayer.setBackgroundResource(R.drawable.btn_stop);
		}
	}
	
	
	private void showOrHide() {
		if (mBottomView.getVisibility() == View.VISIBLE) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_top);
			animation.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
				}
			});

			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mBottomView.setVisibility(View.GONE);
				}
			});
			mBottomView.startAnimation(animation1);
		} else {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_top);
			mBottomView.setVisibility(View.VISIBLE);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_entry_from_bottom);
			mBottomView.startAnimation(animation1);
			mHandler.removeCallbacks(hideRunnable);
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
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
	public int getLayoutId() {
		return R.layout.act_video_player;
	}

	@Override
	public void setRightClick() {
		if (train == null) {
			return;
		}
		// 跳转到评论页面
		Intent intent = new Intent(mContext, CommentActivity.class);
		intent.putExtra(CommentActivity.VALUE_RID, train.getRid());
		startActivity(intent);
	}

	@Override
	public Train getTrain() {
		if (train == null) {
			train = (Train) getIntent().getBundleExtra("train")
					.getSerializable("train");
		}
		return train;
	}

	/** 没有下载文件 **/
	private void statuNotDown() {
		tvPlayer.setVisibility(View.GONE);
		currentTimeTv.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		imgStartDown.setVisibility(View.VISIBLE);
		rotationTv.setVisibility(View.GONE);
		mSeekBar.setEnabled(false);
		mSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
	}

	/** 下载中的 **/
	private void statuDownloading() {
		tvPlayer.setVisibility(View.GONE);
		currentTimeTv.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		imgStartDown.setVisibility(View.GONE);
		rotationTv.setVisibility(View.GONE);
		allTimeTv.setVisibility(View.VISIBLE);
		mSeekBar.setEnabled(false);
		mSeekBar.setThumb(new ColorDrawable(android.R.color.transparent));
	}

	/** 下载完成 **/
	private void statuDownFinish() {
		tvPlayer.setVisibility(View.VISIBLE);
		currentTimeTv.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		imgStartDown.setVisibility(View.GONE);
		rotationTv.setVisibility(View.VISIBLE);
		mSeekBar.setEnabled(true);
		mSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_));
		playVideo();
	}

	private void setVideoHeng() {
		if (mVideo != null & mBottomView != null) {
			int h = mBottomView.getHeight();
			LayoutParams lp = (LayoutParams) mBottomView
					.getLayoutParams();
			lp.topMargin = -h;
			mBottomView.setLayoutParams(lp);
			mVideo.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mVideo.setVideoWidth(screenWidth);
			mVideo.setVideoHeight(screenHeight);
			mVideo.invalidate();
			LayoutParams lps = (LayoutParams) screenView.getLayoutParams();
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			screenView.setLayoutParams(lps);

		}
	}

	private void setVideoShu() {
		if (mVideo != null & mBottomView != null) {
			int h = (int)getResources().getDimension(R.dimen.music_pic_h);
			LayoutParams lp = (LayoutParams) mBottomView
					.getLayoutParams();
			lp.topMargin = 0;
			mBottomView.setLayoutParams(lp);
			mVideo.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, h));
			mVideo.setVideoWidth(screenWidth);
			mVideo.setVideoHeight(h);
			mVideo.invalidate();
			LayoutParams lps = (LayoutParams) screenView.getLayoutParams();
			lps.leftMargin = mraginLR;
			lps.rightMargin = mraginLR;
			screenView.setLayoutParams(lps);
		}
	}
	
	/**
     * 注意，下面的url就是标示符
     * */
    public class MyReceiver extends BroadcastReceiver {
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		handleIntent(intent);
    	}

    	private void handleIntent(Intent intent) {
    		
    		if (intent != null
    				&& intent.getAction().equals("com.badou.mworking.TrainVideoPlayerAct")) {
    			int type = intent.getIntExtra(MyIntents.TYPE, -1);
    			String url;
    			
    			switch (type) {
    			// 添加下载
    			case MyIntents.Types.ADD:
    				url = intent.getStringExtra(MyIntents.URL);
    				break;
    			// 下载完成
    			case MyIntents.Types.COMPLETE:
    				url = intent.getStringExtra(MyIntents.URL);
    				if(url.equals(TrainVideoPlayerAct.this.url)){
    					// 下载完成之后，对文件进行重命名
    					try {
    				        String fileNames[] = url.split("=");
    						FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), fileNames[fileNames.length-1], train.getRid() + ENDWITH_MP4);
    						startPlay();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
        				if (!TextUtils.isEmpty(url)) {
        					statuDownFinish();
        				}
    				}
    				break;
    			// 正在下载
    			case MyIntents.Types.PROCESS:
    				url = intent.getStringExtra(MyIntents.URL);
    				mProgressBar.setVisibility(View.VISIBLE);
    				imgStartDown.setVisibility(View.GONE);
    				if(url.equals(TrainVideoPlayerAct.this.url)){
    					int downPer = Integer.valueOf(intent
        						.getStringExtra(MyIntents.PROCESS_PROGRESS));
        				mSeekBar.setProgress(downPer);
        				allTimeTv.setText(downPer+"%");
    				}
    				break;
    			// 下载出错
    			case MyIntents.Types.ERROR:
    				url = intent.getStringExtra(MyIntents.URL);
    				break;
    			default:
    				break;
    			}
    		}
    	}
    }
}
