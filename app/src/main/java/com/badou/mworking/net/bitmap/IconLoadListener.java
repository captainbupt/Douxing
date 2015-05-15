package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.badou.mworking.R;

public class IconLoadListener implements ImageListener{
	
	private ImageView mImageView;
	private String imgUrl;
	private int defResId;
	
	public IconLoadListener(Context context, ImageView imageView,String imgUrl,int defResId) {
		this.mImageView = imageView;
		this.imgUrl = imgUrl;
		this.defResId = defResId;
	}
	
	public IconLoadListener(Context context, ImageView imageView, int width,
			int height,String imgUrl) {
	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		if (0 == defResId) {
			mImageView.setImageResource(R.drawable.train_item_subject_bg);
		} else {
			mImageView.setImageResource(defResId);
		}
		
	}

	@Override
	public void onResponse(ImageContainer arg0, boolean arg1) {
		Bitmap bmp = arg0.getBitmap();
		if (bmp != null) {
			BitmapLruCache.getBitmapLruCache().putBitmap(imgUrl, bmp);
			mImageView.setImageBitmap(bmp);
		} else {
			if (0 == defResId) {
				mImageView.setImageResource(R.drawable.train_item_subject_bg);
			} else {
				mImageView.setImageResource(defResId);
			}
		}
	}

}
