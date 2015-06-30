package com.badou.mworking.util;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;

/**
 * 功能描述:  SharedPerences保存数据和读取数据封装之后的工具类
 */
public class SPUtil {

    private static Context applicationContext;

    public static void initialize(Context applicationContext) {
        SPUtil.applicationContext = applicationContext;
    }

    public static void clearSP() {
        SP.clearSP(applicationContext, SP.DEFAULTCACHE);
    }

    private static final String KEY_SAVE_INTERNET = "pic_show";

    public static void setSaveInternetOption(boolean isOn) {
        SP.putBooleanSP(applicationContext, SP.DEFAULTCACHE, KEY_SAVE_INTERNET, isOn);
    }

    public static boolean getSaveInternetOption() {
        return SP.getBooleanSP(applicationContext, SP.DEFAULTCACHE, KEY_SAVE_INTERNET, false);
    }

    private static final String PUSH_NOTIFICATIONS = "push_notifications";

    public static void setClosePushOption(boolean isOn) {
        SP.putBooleanSP(applicationContext, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, isOn);
    }

    public static boolean getClosePushOption() {
        return SP.getBooleanSP(applicationContext, SP.DEFAULTCACHE, PUSH_NOTIFICATIONS, false);
    }

    private static final String PDF_PAGE = "pdfpage";

    public static void setPdfPage(String rid, int page) {
        SP.putIntSP(applicationContext, PDF_PAGE, rid, page);
    }

    public static int getPdfPage(String rid) {
        return SP.getIntSP(applicationContext, PDF_PAGE, rid, 1);
    }

    private static final String WEB_SCROLL = "webscroll";

    public static void setWebViewPosition(String url, int y) {
        SP.putIntSP(applicationContext, WEB_SCROLL, url, y);
    }

    public static final int getWebViewPosition(String url) {
        return SP.getIntSP(applicationContext, WEB_SCROLL, url, 1);
    }

    private static final String USER_ACCOUNT = "account";

    public static void setUserAccount(String account) {
        SP.putStringSP(applicationContext, SP.DEFAULTCACHE, USER_ACCOUNT, account);
    }

    public static final String getUserAccount() {
        return SP.getStringSP(applicationContext, SP.DEFAULTCACHE, USER_ACCOUNT, "");
    }

    private static final String USER_INFO = "userinfo";

    public static void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            SP.putStringSP(applicationContext, SP.DEFAULTCACHE, USER_INFO, GsonUtil.toJson(userInfo));
        } else {
            SP.removeSP(applicationContext, SP.DEFAULTCACHE, USER_INFO);
        }
    }

    public static UserInfo getUserInfo() {
        String content = SP.getStringSP(applicationContext, SP.DEFAULTCACHE, USER_INFO, "");
        if (TextUtils.isEmpty(content)) {
            return null;
        } else {
            return GsonUtil.fromJson(content, UserInfo.class);
        }
    }

    private static final String KEY_IS_FIRST = AppApplication.appVersion;

    public static void setIsFirst(boolean isFirst) {
        SP.putBooleanSP(applicationContext, SP.DEFAULTCACHE, KEY_IS_FIRST, isFirst);
    }

    public static boolean getIsFirst() {
        return SP.getBooleanSP(applicationContext, SP.DEFAULTCACHE, KEY_IS_FIRST, true);
    }
}
