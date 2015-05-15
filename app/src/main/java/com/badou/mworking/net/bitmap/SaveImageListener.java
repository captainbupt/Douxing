package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.badou.mworking.util.FileUtils;

public class SaveImageListener implements ImageListener{
	private ImageView mImageView;
	private String imgUrl;
	private String finalImgPath;
	
	
	public SaveImageListener(Context context, ImageView imageView,String imgUrl,String finalImgPath) {
		this.mImageView = imageView;
		this.imgUrl = imgUrl;
		this.finalImgPath = finalImgPath;
	}
	
	public SaveImageListener(Context context, ImageView imageView, int width,
			int height,String imgUrl) {
	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		mImageView.setVisibility(View.GONE);
	}

	@Override
	public void onResponse(ImageContainer arg0, boolean arg1) {
		Bitmap bmp = arg0.getBitmap();
		if (bmp != null) {
			BitmapLruCache.getBitmapLruCache().putBitmap(imgUrl, bmp);
			mImageView.setImageBitmap(bmp);
			FileUtils.writeBitmap2SDcard(bmp, finalImgPath);
			mImageView.setVisibility(View.VISIBLE);
		} else {
			mImageView.setVisibility(View.GONE);
		}
	}

}
