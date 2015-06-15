package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class NormalImageListener implements ImageListener {

    private ImageView mImageView;
    private String mImgUrl;
    private int mDefaultResId;

    public NormalImageListener(ImageView imageView, String imgUrl, int defResId) {
        this.mImageView = imageView;
        this.mImgUrl = imgUrl;
        this.mDefaultResId = defResId;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        mImageView.setImageResource(mDefaultResId);
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
        Bitmap bmp = imageContainer.getBitmap();
        if (bmp != null) {
            BitmapLruCache.getBitmapLruCache().putBitmap(mImgUrl, bmp);
            mImageView.setImageBitmap(bmp);
        } else {
            mImageView.setImageResource(mDefaultResId);
        }
    }

}
