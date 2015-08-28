package com.badou.mworking.net;

import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;

/**
 * 功能描述:  接口工具类
 */
public class Net {

    public static final String DATA = "data";
    public static final String CODE = "errcode";
    public static final int SUCCESS = 0;
    public static final int LOGOUT = 50002;

    public static final String FAQ = "/faq.html";

    public static final String Http_Host_ip = "http://115.28.138.79";

    public static String getRunHost() {
        return Http_Host_ip + "/badou";
    }


    /**
     * 功能描述:考试item 点击调取的web
     *
     * @param uid
     * @param rid
     * @return
     */
    public static String EXAM_ITEM(String uid, String rid) {
        return "/doexam?sys=android"
                + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&rid=" + rid;
    }

    public static String getWeiDiaoYanURl() {
        return "http://mworking.cn/badou/dofeed?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion + "&uid=";
    }

    /**
     * @return 获取统计url
     */
    public static String getTongji(String uid, String rid) {
        return "/dostat?sys=android" + AppApplication.SYSVERSION + "&ver=" + AppApplication.appVersion
                + "&uid=" + uid + "&rid=" + rid;
    }

    public static String getLevelUrl(String uid) {
        return "http://ops.mworking.cn/webview/userTitle.html?uid=" + uid;
    }

    public static String getCreditUrl(String uid, int credit) {
        return "http://ops.mworking.cn/webview/creditTitle.html?uid=" + uid + "&credit=" + credit;
    }
}
