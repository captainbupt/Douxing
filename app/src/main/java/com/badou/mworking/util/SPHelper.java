package com.badou.mworking.util;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.entity.user.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:  SharedPerences保存数据和读取数据封装之后的工具类
 */
public class SPHelper {

    private static Context applicationContext;

    public static void initialize(Context applicationContext) {
        SPHelper.applicationContext = applicationContext;
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
        System.out.println(content);
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

    private static final String CONTACT_LIST_LAST_UPDATE_TIME = "contactlistlastupdate";

    public static void setContactLastUpdateTime(long time) {
        String uid = UserInfo.getUserInfo().getUid();
        SP.putLongSP(applicationContext, SP.DEFAULTCACHE, CONTACT_LIST_LAST_UPDATE_TIME + uid, time);
    }

    public static long getContactLastUpdateTime() {
        String uid = UserInfo.getUserInfo().getUid();
        return SP.getLongSP(applicationContext, SP.DEFAULTCACHE, CONTACT_LIST_LAST_UPDATE_TIME + uid, 1);
    }

    private static final String LOGO_URL = "logoUrl";

    public static void setLogoUrl(String logoUrl) {
        SP.putStringSP(applicationContext, SP.DEFAULTCACHE, LOGO_URL, logoUrl);
    }

    public static String getLogoUrl() {
        return SP.getStringSP(applicationContext, SP.DEFAULTCACHE, LOGO_URL, "");
    }

    // private static final String MAIN_BANNER = "banner";  1.6.0之前banner缓存的key，存储方式不一致，不要轻易使用
    private static final String MAIN_BANNER = "bannerlist";

    public static void setMainBanner(List<MainBanner> bannerList) {
        String content = GsonUtil.toJson(bannerList, new TypeToken<List<MainBanner>>() {
        }.getType());
        SP.putStringSP(applicationContext, SP.DEFAULTCACHE, MAIN_BANNER, content);
    }

    public static List<MainBanner> getMainBanner() {
        String bannerStr = SP.getStringSP(applicationContext, SP.DEFAULTCACHE, MAIN_BANNER, "");
        if (TextUtils.isEmpty(bannerStr)) {
            return new ArrayList<>();
        } else {
            return (List<MainBanner>) GsonUtil.fromJson(bannerStr, new TypeToken<List<MainBanner>>() {
            }.getType());
        }
    }
}
