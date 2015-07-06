package com.badou.mworking.util;

import android.content.Context;

import com.badou.mworking.base.AppApplication;

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

    public static void setClosePushOption(Context context, boolean isOn) {
        SP.putBooleanSP(context, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, isOn);
    }

    public static boolean getClosePushOption(Context context) {
        return SP.getBooleanSP(context, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, false);
    }

    public static final String PDF_PAGE = "pdfpage";

    public static void setPdfPage(Context context, String rid, int page) {
        SP.putIntSP(context, PDF_PAGE, rid, page);
    }

    public static int getPdfPage(Context context, String rid) {
        return SP.getIntSP(context, PDF_PAGE, rid, 1);
    }

    public static final String WEB_SCROLL = "webscroll";

    public static void setWebViewPosition(Context context, String url, int y) {
        SP.putIntSP(context, WEB_SCROLL, url, y);
    }

    public static final int getWebViewPosition(Context context, String url) {
        return SP.getIntSP(context, WEB_SCROLL, url, 1);
    }

    public static final String CONTACT_LIST_LAST_UPDATE_TIME = "contactlistlastupdate";

    public static void setContactLastUpdateTime(Context context, long time) {
        String uid = ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
        SP.putLongSP(context, SP.DEFAULTCACHE, CONTACT_LIST_LAST_UPDATE_TIME + uid, time);
    }

    public static final long getContactLastUpdateTime(Context context) {
        String uid = ((AppApplication) context.getApplicationContext()).getUserInfo().userId;
        return SP.getLongSP(context, SP.DEFAULTCACHE, CONTACT_LIST_LAST_UPDATE_TIME + uid, 1);
    }

}
