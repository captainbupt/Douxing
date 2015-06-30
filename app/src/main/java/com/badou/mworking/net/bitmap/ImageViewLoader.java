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
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPUtil;

/**
 * Created by Administrator on 2015/6/8.
 */
public class ImageViewLoader {

    public static void setCircleImageViewResource(ImageView imageView, String url, int size) {
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(url);
        if (headBmp != null && !headBmp.isRecycled()) {
            imageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(url, new CircleImageListener(url, imageView, size, size), size, size);
        }
    }

    public static void setSquareImageViewResource(final ImageView imageView, int defaultResId, final String url, int size) {
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getBitmap(url);
        if (headBmp != null && !headBmp.isRecycled()) {
            imageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(url, new NormalImageListener(imageView, url, R.drawable.icon_image_default), size, size);
        }
    }

    public static void setSquareImageViewResourceOnWifi(Context context, ImageView imageView, int defaultResId, String url, int size) {
        if (TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.GONE);
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        //评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(context);
        boolean isSaveInternet = SPUtil.getSaveInternetOption();
        Bitmap contentBmp = BitmapLruCache.getBitmapLruCache().get(url);
        if (contentBmp != null && contentBmp.isRecycled()) {
            imageView.setImageBitmap(contentBmp);
        } else {
//            if (isWifi || !isSaveInternet) {
            setSquareImageViewResource(imageView, defaultResId, url, size);
/*            } else {
                imageView.setImageResource(R.drawable.icon_image_default);
            }*/
        }
    }

    public static void setImageViewResource(final ImageView imageView, final int defaultRes, final String url) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(defaultRes);
            return;
        }
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getBitmap(url);
        if (headBmp != null && !headBmp.isRecycled()) {
            imageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(url, new NormalImageListener(imageView, url, defaultRes));
        }
    }
}
