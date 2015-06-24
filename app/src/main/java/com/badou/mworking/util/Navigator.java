package com.badou.mworking.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;

/**
 * Created by Administrator on 2015/6/24 0024.
 */
public class Navigator {
    public static void toLoginPage(Activity activity) {
        UserInfo.clearUserInfo(activity.getApplicationContext());
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
