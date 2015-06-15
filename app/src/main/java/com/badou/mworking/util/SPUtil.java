package com.badou.mworking.util;

import android.content.Context;

/**
 * 功能描述:  SharedPerences保存数据和读取数据封装之后的工具类
 */
public class SPUtil {

    public static final String KEY_SAVE_INTERNET = "pic_show";

    public static void setSaveInternetOption(Context context, boolean isOn) {
        SP.putBooleanSP(context, SP.DEFAULTCACHE, KEY_SAVE_INTERNET, isOn);
    }

    public static boolean getSaveInternetOption(Context context) {
        return SP.getBooleanSP(context, SP.DEFAULTCACHE, KEY_SAVE_INTERNET, false);
    }

    public static final String PUSH_NOTIFICATIONS = "push_notifications";

    public static void setPushOption(Context context, boolean isOn) {
        SP.putBooleanSP(context, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, isOn);
    }

    public static boolean getPushOption(Context context) {
        return SP.getBooleanSP(context, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, true);
    }
}
