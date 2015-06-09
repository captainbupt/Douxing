package com.badou.mworking.net.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.badou.mworking.R;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.NetUtils;

/**
 * Created by Administrator on 2015/6/8.
 */
public class ImageViewLoader {

    public static void setCircleImageViewResource(Context context, ImageView imageView, String url, int size) {
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(url);
        if (headBmp != null && !headBmp.isRecycled()) {
            imageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(url, new CircleImageListener(context, url, imageView, size, size), size, size);
        }
    }

    public static void setSquareImageViewResource(Context context, final ImageView imageView, final String url, int size) {
        imageView.setImageResource(R.drawable.icon_image_default);
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getBitmap(url);
        if (headBmp != null && !headBmp.isRecycled()) {
            imageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(url, new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap bmp = imageContainer.getBitmap();
                    if (bmp != null) {
                        BitmapLruCache.getBitmapLruCache().putBitmap(url, bmp);
                        imageView.setImageBitmap(bmp);
                    }
                }
            }, size, size);
        }
    }

    public static void setSquareImageViewResourceOnWifi(Context context, ImageView imageView, String url, int size) {
        if (TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.GONE);
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        //评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(context);
        Bitmap contentBmp = BitmapLruCache.getBitmapLruCache().get(url);
        if (contentBmp != null && contentBmp.isRecycled()) {
            imageView.setImageBitmap(contentBmp);
        } else {
            if (isWifi) {
                setSquareImageViewResource(context, imageView, url, size);
            } else {
                imageView.setImageResource(R.drawable.icon_image_default);
            }
        }
    }
}
