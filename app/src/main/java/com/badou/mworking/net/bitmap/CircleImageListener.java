package com.badou.mworking.net.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.badou.mworking.R;
import com.badou.mworking.util.BitmapUtil;

public class CircleImageListener implements ImageListener {

    private ImageView mImageView;
    private String mImgUrl;
    private int mWidth;
    private int mHeight;

    public CircleImageListener(String imgUrl, ImageView imageView, int width, int height) {
        mImageView = imageView;
        this.mWidth = width;
        this.mHeight = height;
        this.mImgUrl = imgUrl;
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        mImageView.setImageResource(R.drawable.icon_user_detail_default_head);
    }

    @Override
    public void onResponse(ImageContainer arg0, boolean arg1) {
        Bitmap bmp = arg0.getBitmap();
        if (bmp != null) {
            BitmapLruCache.getBitmapLruCache().putBitmap(mImgUrl, bmp);
            Bitmap headBmp = BitmapUtil.getCirlBitmp(bmp, mWidth, mHeight);
            BitmapLruCache.getBitmapLruCache().putCircleBitmap(mImgUrl, headBmp);
            mImageView.setImageBitmap(headBmp);
        } else {
            mImageView.setImageResource(R.drawable.icon_user_detail_default_head);
        }
    }

}
