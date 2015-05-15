package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OptimizedImageView extends ImageView {

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		boolean resizeWidth = widthSpecMode == MeasureSpec.EXACTLY;
		boolean resizeHeight = heightSpecMode == MeasureSpec.EXACTLY;
		if (resizeWidth && resizeHeight) {
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
	}

	private int width;
	private int height;

	public OptimizedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
