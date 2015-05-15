/* 
 * 文件名: SheXActivity.java
 * 包路径: com.badou.mworking
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月15日 下午1:58:52
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月15日 下午1:58:52 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.util.CameraPreview;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * 类:  <code> SheXActivity </code>
 * 功能描述: 摄像页面
 * 创建人:  葛建锋
 * 创建日期: 2015年1月15日 下午1:58:52
 * 开发环境: JDK7.0
 */
public class SheXActivity extends Activity implements SurfaceHolder.Callback {
	
	private SurfaceHolder mSurfaceHolder;
	private MediaRecorder mMediaRecorder;
	private CheckBox playOrStop;
	private Camera mCamera;
	private CameraPreview mSurfaceView;
	private TextView recor_mp4_left_time;
	private int second = 10;
	
	/**三星上不支持QUALITY_CIF,QUALITY_QVGA*/
	private static final String saveName = "douxing_paishe"; 
	public static  String sheXPath="";

	Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置横屏显示
		setContentView(R.layout.shexactivity);
		handler = new Handler();
		// 设置输出路径
		SheXActivity.sheXPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + saveName + ".mp4";
		File file = new File(sheXPath);
		if (file.exists()) {
			file.delete();
		}
		init();
		recor_mp4_left_time.setText(second + "秒");

	}

	private void init() {
		playOrStop = (CheckBox) findViewById(R.id.etsound_playstop);
		playOrStop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if(isChecked){   // 表示选中，现在按钮显示完成，开始摄像
					if (mMediaRecorder == null) {
						try {
							initMediaRecorder();
							mMediaRecorder.prepare();
							mMediaRecorder.start();

							handler.postDelayed(runnable, 0);
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					 //此处避免用户点击了两次，就是点击拍照，开始，然后点击完成，按钮的变化速度要快于，视屏完结的生成速度，
					// 此时用户再次点击开始，又快速点击完成，导致时间太短，视屏生成不了（ps：特殊操作，此处做个保护）
					playOrStop.setEnabled(false);  
					try {
						if (mMediaRecorder != null) {
							mMediaRecorder.stop();
							releaseMediaRecorder();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					finish();
				}
			}
		});
		recor_mp4_left_time = (TextView) findViewById(R.id.recor_mp4_left_time);
		mCamera = getCameraInstance();
		// 创建Preview view并将其设为activity中的内容
		mSurfaceView = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mSurfaceView);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			second--;
			if (second < 0) {
				handler.removeCallbacks(this);
			} else {
				recor_mp4_left_time.setText(second + "秒");
				handler.postDelayed(this, 1000);
			}
		}
	};

	@SuppressLint("NewApi")
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			// 获取Camera实例
			c = Camera.open(0);
			c.setDisplayOrientation(90);
		} catch (Exception e) {
			// 摄像头不可用（正被占用或不存在）
			Toast.makeText(getApplicationContext(), "打不开摄像头,快去试试能打开相机吗",
					Toast.LENGTH_LONG).show();
		}
		// 不可用则返回null
		return c;

	}

	@SuppressLint("NewApi")
	private void initMediaRecorder() {
		mMediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setOrientationHint(90);// 视频旋转90度
		/**视频 音频 源**/
		setMediaPara();
		// 设置视频的最大持续时间
		mMediaRecorder.setMaxDuration(second * 1000);
		// 为MediaRecorder设置监听
		mMediaRecorder.setOnInfoListener(new OnInfoListener() {
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					try {
						if (mMediaRecorder != null) {
//							Thread.sleep(1000);
							//设置后不会崩
				            mMediaRecorder.setOnErrorListener(null);
				            mMediaRecorder.setPreviewDisplay(null);
							mMediaRecorder.stop();
							releaseMediaRecorder();
						}
					} catch (Exception e) {
						mMediaRecorder.release();
						mMediaRecorder = null;
						e.printStackTrace();
					}
					finish();
				}
			}
		});
	}
	
	@SuppressLint("InlinedApi")
	private void setMediaPara(){
		CamcorderProfile profile = CamcorderProfile
	            .get(CamcorderProfile.QUALITY_LOW);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音麦
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

	    mMediaRecorder.setVideoEncodingBitRate(500000);
		mMediaRecorder.setVideoFrameRate(20);
	    mMediaRecorder.setVideoSize(720,
	    		480); 
	    mMediaRecorder.setAudioChannels(profile.audioChannels);
	    mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
	    mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
	    mMediaRecorder.setAudioEncoder(profile.audioCodec);
	    mMediaRecorder.setVideoEncoder(profile.videoCodec);
//		mMediaRecorder.setProfile(profile);

	    try {
			String s =  profile.videoBitRate + " | " + profile.videoFrameRate + " | " +profile.videoFrameWidth+"*"
			        +profile.videoFrameHeight +" | " +profile.audioChannels +" | "+ profile.audioBitRate + " | " + 
			        profile.audioSampleRate + " | " +profile.audioCodec + " | " + profile.videoCodec;
		} catch (Exception e) {
			e.printStackTrace();
		}
	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(sheXPath);

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
	}

	private class ClickListenerImpl implements OnClickListener {

		public void onClick(View v) {

			CheckBox checkBox = (CheckBox) v;
			if (checkBox.getId() == R.id.etsound_playstop) {
				if (mMediaRecorder == null) {
					try {
						initMediaRecorder();
						mMediaRecorder.prepare();
						mMediaRecorder.start();

						handler.postDelayed(runnable, 0);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (mMediaRecorder != null) {
							mMediaRecorder.stop();
							releaseMediaRecorder();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					finish();
				}
			}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(runnable);
		// 如果正在使用MediaRecorder，首先需要释放它。
		releaseMediaRecorder();
		// 在暂停事件中立即释放摄像头
		releaseCamera();
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			// 清除recorder配置
			handler.removeCallbacks(runnable);
			mMediaRecorder.reset();
			// 释放recorder对象
			mMediaRecorder.release();
			mMediaRecorder = null;
			// 为后续使用锁定摄像头
			mCamera.lock();
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			// 为其它应用释放摄像头
			mCamera.release();
			mCamera = null;
		}
	}

	// 在 surfaceChanged以后，才在 initMediaRecorder()方法里 MediaRecorder 变量设置参数，不然 会在
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
//		System.out.println("SurfaceView---->Changed");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("SurfaceView---->Destroyed");
		if (mMediaRecorder != null) {
			releaseMediaRecorder();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}
	
	
}
