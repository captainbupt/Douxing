package com.badou.mworking.view;

import android.graphics.Bitmap;

import com.badou.mworking.net.bitmap.BitmapLruCache;

import java.util.List;

public interface ChatterSubmitView extends BaseView{
    void setTopicListVisibility(boolean isVisible);
    void setAnonymousCheckBox(boolean isChecked);
    void takeImage();
    void addImage(Bitmap bitmap);
    void addVideo(Bitmap bitmap, String path);
    int getMaxImageCount();
    List<Bitmap> getCurrentBitmap();
    void clearBitmap();
    void onTopicSelected(String topic);
    void setImageMode(boolean isVideo);
}
