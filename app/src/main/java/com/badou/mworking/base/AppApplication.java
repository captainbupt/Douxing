package com.badou.mworking.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.CrashHandler;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.SPUtil;
import com.baidu.mapapi.SDKInitializer;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 程序入口application类
 */
public class AppApplication extends Application {

    // tag
    public static String appVersion;
    public static final String SYSTYPE = "android";
    public static final String SYSVERSION = android.os.Build.VERSION.RELEASE;
    public static final String SYSPARAM = SYSTYPE + SYSVERSION;

    @Override
    public void onCreate() {
        super.onCreate();

        // Bitmap初始化必须在MyVolley之前，否则会丢出异常
        BitmapLruCache.init(getApplicationContext());
        MyVolley.init(getApplicationContext());
        //开启异常捕获
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //获取程序版本
        try {
            appVersion = getVersionName();
        } catch (Exception e) {
            appVersion = "1.0";
            e.printStackTrace();
        }

        SPUtil.initialize(this);
        GsonUtil.initialize();

        MTrainingDBHelper.init(getApplicationContext());
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
                0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 功能描述: 更换语言
     *
     * @param resources
     * @param lanAtr
     */
    public static void changeAppLanguage(Resources resources, String lanAtr) {
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (lanAtr.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (lanAtr.equals("zh")) {
            config.locale = Locale.CHINA;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 功能描述:退出登录
     *
     * @param context
     */
    public static void logoutShow(final Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.message_tips)
                .setMessage(R.string.tips_reLogin_msg).setCancelable(false)
                .setPositiveButton(R.string.text_ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //退出登录
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }).show();
    }
}
