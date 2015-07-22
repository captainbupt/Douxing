package com.badou.mworking.view;

import android.graphics.Bitmap;

import com.badou.mworking.entity.user.UserDetail;

public interface UserCenterView extends BaseView {
    void takeImage();
    void setData(UserDetail userDetail);
    void setHeadImage(String url);
}
