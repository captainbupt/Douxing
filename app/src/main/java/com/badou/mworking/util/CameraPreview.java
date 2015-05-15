package com.badou.mworking.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements Callback {
	private static final String TAG = "TAG-CameraPreview";

	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// 安装一个SurfaceHolder.Callback
		mHolder = getHolder();
		mHolder.addCallback(this);

		// 针对低于3.0的Android
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 把预览画面的位置通知摄像头
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.w(TAG, "Error setting camera preview: " + e.getMessage());
		} catch (NullPointerException e) {
			// TODO: handle exception
			Log.w(TAG, "打不开摄像头 == " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (mHolder.getSurface() == null) {
			// 预览surface不存在
			return;
		}

		// 更改时停止预览
		try {
			mCamera.stopPreview();
		} catch (Exception e) {

		}

		// 在此进行缩放、旋转和重新组织格式
		// 以新的设置启动预览
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}
