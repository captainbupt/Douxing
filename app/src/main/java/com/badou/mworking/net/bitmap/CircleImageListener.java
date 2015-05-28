package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.badou.mworking.R;
import com.badou.mworking.util.BitmapUtil;

public class CircleImageListener implements ImageListener {
	
	private ImageView mImageView;
	private String imgUrl;
	private int width;
	private int height;
	private Context mContext;

	public CircleImageListener(Context context,String imgUrl, ImageView imageView, int width,
			int height) {
		mContext = context;
		mImageView = imageView;
		this.width = width;
		this.height = height;
		this.imgUrl = imgUrl;
	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		// TODO Auto-generated method stub
		setDefaultHead();
	}

	@Override
	public void onResponse(ImageContainer arg0, boolean arg1) {
		// TODO Auto-generated method stub
		Bitmap bmp = arg0.getBitmap();
		if (bmp != null) {
			Bitmap headBmp = BitmapUtil.getCirlBitmp(bmp, width, height);
			BitmapLruCache.getBitmapLruCache().putCircleBitmap(imgUrl, headBmp);
			mImageView.setImageBitmap(headBmp);
		} else {
			setDefaultHead();
		}
	}

	private void setDefaultHead() {
		// TODO Auto-generated method stub
		Resources res = mContext.getResources();
		mImageView.setImageBitmap(BitmapUtil.getCirlBitmp(BitmapUtil
				.decodeSampledBitmapFromResource(res,
						R.drawable.icon_user_detail_default_head, width, height),
				width, height));
	}

}
