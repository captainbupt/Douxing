package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class PicImageListener implements ImageListener{
	
	private ImageView mImageView;
	private String imgUrl;
	private TextView tv;
	private ImageView downImg = null; //当图片加载完成之后，图片直接显示
	
	public PicImageListener(Context context, ImageView imageView,String imgUrl,ImageView downImg) {
		this.mImageView = imageView;
		this.imgUrl = imgUrl;
		this.downImg = downImg;
	}
	
	public PicImageListener(Context context, ImageView imageView,String imgUrl) {
		this.mImageView = imageView;
		this.imgUrl = imgUrl;
	}
	
	public PicImageListener(Context context, ImageView imageView, String imgUrl,TextView tv) {
		this.mImageView = imageView;
		this.imgUrl = imgUrl;
		this.tv = tv;
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
			mImageView.setVisibility(View.VISIBLE);
			if (tv!=null) {
				tv.setBackgroundColor(Color.TRANSPARENT);
			}
			if(downImg!=null){
				downImg.setVisibility(View.VISIBLE);
			}
		} else {
			mImageView.setVisibility(View.GONE);
			if (tv!=null) {
				tv.setBackgroundColor(Color.WHITE);
			}
			if(downImg!=null){
				downImg.setVisibility(View.GONE);
			}
		}
	}
}
