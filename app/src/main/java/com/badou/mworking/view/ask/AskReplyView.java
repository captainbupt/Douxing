package com.badou.mworking.view.ask;

import android.graphics.Bitmap;

import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.view.BaseView;

public interface AskReplyView extends BaseView{
    void addImage(Bitmap bitmap);
    void takeImage();
}
