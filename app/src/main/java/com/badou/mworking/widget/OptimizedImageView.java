package com.badou.mworking.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.badou.mworking.util.BitmapUtil;

public class OptimizedImageView extends ImageView {

	private Context mContext;

	public OptimizedImageView(Context context) {
		super(context);
		this.mContext = context;
	}

	public OptimizedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void setImageResourceFullScreen(int resId){
		WindowManager manage = ((Activity)mContext).getWindowManager();
		Display display = manage.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		setImageResource(resId, screenWidth, screenHeight);
	}

	public void setImageResource(int resId, int width, int height){
		setScaleType(ScaleType.CENTER_CROP);
		setImageBitmap(BitmapUtil
				.decodeSampledBitmapFromResource(getResources(),
						resId, width,
						height));
	}

}
