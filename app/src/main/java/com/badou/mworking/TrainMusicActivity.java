package com.badou.mworking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Train;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.MyIntents;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类:  <code> TrainMusicActivity </code>
 * 功能描述: 微培训
 * 创建人:  葛建锋
 * 创建日期: 2015年1月8日 下午7:36:49
 * 开发环境: JDK7.0
 */
public class TrainMusicActivity extends TrainBaseActivity{
	
	private TextView tvPlayer; //开始/暂停 播放 
	private TextView tvTop; // 标题
	private TextView musicFileSizeTv; // 音乐文件大小
	
	/** 可拖动进度条 **/
	private SeekBar seekbar;
	/** 正在播放 **/
	private boolean isPlay = false;

	/** 媒体播放 **/
	private MediaPlayer player = null;

	private boolean iffirst = false;
	/** 计时器 */
	private Timer mTimer;
	/** 异步计时 **/
	private TimerTask mTimerTask;
	private boolean isChanging = false;// 互斥变量，防止定时器与SeekBar拖动时进度冲突
	/** 总时长 **/
	private TextView allTimeTv;
	/** 当前时长 **/
	private TextView currentTimeTv;
	/** 点击下载文件的按钮 **/
	private ImageView imgDown;
	/** 主线程 **/
	private MyHadler myHadler;
	/** 实体类 **/
	private Train train;
	/** 要打开的文件 **/
	private File file;
	/** 保存文件的路径,无文件名 **/
	private String saveFilePath;
	/** 下载中显示的进度条 **/
	private ProgressBar mProgressBar;
	/** 格式化时间 **/
	private SimpleDateFormat dateFormat;
	/*** 下载中 **/
	public static final int STATU_LONDING = 1;
	/** 下载完成 **/
	public static final int STATU_LONDFINISH = 2;
	/** 播放计时器 **/
	public static final int STATU_START_PLAY = 5;
	
	private static final int FILE_SIZE = 6;
	public static String filesize = "";

	/** MP3后缀名 **/
	public static final String ENDWITH_MP3 = ".mp3";
	
	private String url = "";

	/** 记录文件的下载状态 **/
	public static int mediaDownloadStatu = STATU_LONDFINISH;
	
    private MyReceiver mReceiver;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		dateFormat = new SimpleDateFormat("mm:ss");
		player = new MediaPlayer();
		initView();// 各组件
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
				tvPlayer.setBackgroundResource(R.drawable.btn_start);
				isPlay = false;
			}
		}
	}

	/****
	 * 
	 * 功能描述:初始化控件和路径
	 */
	private void initView() {
		saveFilePath = FileUtils.getTrainCacheDir(TrainMusicActivity.this)+train.getRid() + ENDWITH_MP3;
		file = new File(saveFilePath);
		tvPlayer = (TextView) findViewById(R.id.tvPlayer);
		tvTop = (TextView) findViewById(R.id.title_tv);
		allTimeTv = (TextView) findViewById(R.id.tvTotalTime);
		currentTimeTv = (TextView) findViewById(R.id.tvCurrentTime);
		imgDown = (ImageView) findViewById(R.id.imgDownLoad);
		musicFileSizeTv = (TextView) findViewById(R.id.music_file_size_tv);
		tvPlayer.setOnClickListener(new MyClick());
		mProgressBar = (ProgressBar) findViewById(R.id.pro_downloading);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setOnSeekBarChangeListener(new MySeekbar());
		try {
			train = (Train) getIntent().getBundleExtra("train").getSerializable(
					"train");
			String title = train.getSubject();
			if(!TextUtils.isEmpty(title)){
				tvTop.setText(train.getSubject());
			}
			url = train.getUrl()
					+ "&uid="
					+ ((AppApplication) mContext.getApplicationContext())
							.getUserInfo().getUserId();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						// 如果文件已经存在则直接获取文件大写，如果文件不存在则进行网络请求
						if(file.exists()){
							float fileLength =  file.length();
							filesize = (float)(Math.round(fileLength/1024/1024*10))/10+"M";
						}else{
							URL DownRul = new URL(url);
							HttpURLConnection urlcon = (HttpURLConnection) DownRul.openConnection();
							float fileLength =  urlcon.getContentLength();
							filesize = (float)(Math.round(fileLength/1024/1024*10))/10+"M";
						}
						myHadler.sendEmptyMessage(FILE_SIZE);
						} catch (Exception e) {
							e.printStackTrace();
						} 
				}
			}).start();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		myHadler = new MyHadler();
		
		// 这里加个保护，就是在下载中，退出界面，广播也关了，下载完之后没有重命名
		String fileNames[] = url.split("=");
		FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), fileNames[fileNames.length-1], train.getRid() + ".mp3");
		// 文件存在，下载完成
		if (file.exists()) {
			statuDownloadFinish();
		} else {
			statuNotDownLoad();
			allTimeTv.setText("0%");
		}
		
		ivLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TrainMusicActivity.this.finish();
			}
		});
		
		imgDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (NetUtils.isNetConnected(mContext)) {
					/** 开始下载 **/
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
		
		  //开启下载服务，进行的是开始下载操作
        Intent downloadIntent = new Intent("com.badou.mworking.services.IDownloadService");
        downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
        startService(downloadIntent);

        //广播，接收
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.badou.mworking.TrainVideoPlayerAct");
        registerReceiver(mReceiver, filter);
	}
	
	/***
	 * 
	 * 功能描述:初始化播放器
	 */
	private void initMedia() {
		if(player == null){
			return;
		}
		try {
			player.reset();
			player.setDataSource(file.getAbsolutePath());
			player.prepare();// 就是把存储卡中的内容全部加载或者网络中的部分媒体内容加载到内存中，有可能会失败抛出异常的
		} catch (Exception e) {
			e.printStackTrace();
		}
		seekbar.setMax(player.getDuration());// 设置进度条
		seekbar.setProgress(0);
		String alltime = dateFormat.format(player.getDuration());
		allTimeTv.setText(alltime);
		currentTimeTv.setText("00:00");
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				tvPlayer.setBackgroundResource(R.drawable.btn_start);
				isPlay = false;
				initMedia();
			}
		});
	}

	/***
	 * 
	 * 类: <code> MyClick </code> 功能描述: 按钮监听 创建日期: 2015年1月7日
	 * 下午2:36:11 开发环境: JDK6.0
	 */
	class MyClick implements OnClickListener {
		public void onClick(View v) {
			// 判断有没有要播放的文件
			if (file.exists() && player != null) {
				switch (v.getId()) {
				case R.id.tvPlayer:
					if (player != null && !isPlay) {
						startPlay(v);
					} else if (isPlay) {
						// tvPlayer.setText("继续");
						player.pause();
						isPlay = false;
						v.setBackgroundResource(R.drawable.btn_start);
					}
					break;
				}
			}
		}
	}

	/**
	 * 功能描述: 开始播放
	 */
	private void startPlay(View v){
		// tvPlayer.setText("暂停");
		if (!iffirst) {
			// ----------定时器记录播放进度---------//
			mTimer = new Timer();
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (isChanging == true) {
						return;
					}
					myHadler.sendEmptyMessage(STATU_START_PLAY);
				}
			};
			mTimer.schedule(mTimerTask, 0, 10);
			iffirst = true;
		}

		player.start();// 开始
		isPlay = true;
		v.setBackgroundResource(R.drawable.btn_stop);
	}
	
	
	// 进度条处理
	class MySeekbar implements OnSeekBarChangeListener {
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			isChanging = true;
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			player.seekTo(seekbar.getProgress());
			isChanging = false;
		}

	}

	// 来电处理
	@Override
	protected void onDestroy() {
		relasePlayer();
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void relasePlayer(){
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
				if (player != null) {
					String currentTime = dateFormat.format(player
							.getCurrentPosition());
					currentTimeTv.setText(currentTime);
					seekbar.setProgress(player.getCurrentPosition());
				}
				break;
			case FILE_SIZE:
				musicFileSizeTv.setText("音频文件("+filesize+")");
				break;
			default:
				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.act_music_player;
	}

	@Override
	public void setRightClick() {
		if (train == null) {
			return;
		}
		//  跳转到评论页面
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
	
	/** 没下载的状态 **/
	private void statuNotDownLoad() {
		imgDown.setVisibility(View.VISIBLE);
		tvPlayer.setVisibility(View.GONE);
		currentTimeTv.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		seekbar.setEnabled(false);// 下载中禁止用户拖动
		seekbar.setThumb(new ColorDrawable(android.R.color.transparent));
	}

	/** 下载完成,可以播放的状态 **/
	private void statuDownloadFinish() {
		imgDown.setVisibility(View.GONE);
		tvPlayer.setVisibility(View.VISIBLE);
		currentTimeTv.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		seekbar.setEnabled(true);
		seekbar.setThumb(getResources().getDrawable(R.drawable.seekbar_));
		/** 下载完成,初始化播放器 **/
		initMedia();
	}

	/** 下载中 **/
	private void statuDownloading() {
		imgDown.setVisibility(View.GONE);
		tvPlayer.setVisibility(View.GONE);
		currentTimeTv.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		seekbar.setEnabled(false);// 下载中禁止用户拖动
		seekbar.setThumb(new ColorDrawable(android.R.color.transparent));
	}
	
	/**
	 * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TrainMusicActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
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
    				if(url.equals(TrainMusicActivity.this.url)){
    					// 下载完成之后，对文件进行重命名
    					try {
    				        String fileNames[] = url.split("=");
    						FileUtils.renameFile(FileUtils.getTrainCacheDir(mContext), fileNames[fileNames.length-1], train.getRid() + ".mp3");
    						statuDownloadFinish();
    						startPlay(tvPlayer);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				break;
    			// 正在下载
    			case MyIntents.Types.PROCESS:
    				url = intent.getStringExtra(MyIntents.URL);
    				if(url.equals(TrainMusicActivity.this.url)){
    					int downPer = Integer.valueOf(intent
        						.getStringExtra(MyIntents.PROCESS_PROGRESS));
    					imgDown.setVisibility(View.GONE);
    					mProgressBar.setVisibility(View.VISIBLE);
    					seekbar.setProgress(downPer);
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
