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
import com.badou.mworking.entity.user.UserInfoTmp;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.CrashHandler;
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
    public static final String SYSVERSION = android.os.Build.VERSION.RELEASE;
    private UserInfoTmp userInfo;

    public static String screenlg = "md";

    private static AppApplication appApplication;

    //公开，静态的工厂方法
    public static AppApplication getInstance() {
        if (appApplication == null) {
            appApplication = new AppApplication();
        }
        return appApplication;
    }

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

        MTrainingDBHelper.init(getApplicationContext());
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        getScreenLevel();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    /**
     * 功能描述:  获取用户信息
     *
     * @return 用户信息类的对象实体
     */
    public UserInfoTmp getUserInfo() {
        if (userInfo == null) {
            userInfo = UserInfoTmp.getUserInfoFromSP(this);
        }
        return userInfo;
    }

    /**
     * 功能描述:  设置保存用户对象信息
     *
     * @param userInfo
     */
    public void setUserInfo(UserInfoTmp userInfo) {
        this.userInfo = userInfo;
        userInfo.saveUserInfo(getApplicationContext());
        Set<String> tags = new HashSet<>();
        tags.add(userInfo.tag);
        if (JPushInterface.isPushStopped(this))
            JPushInterface.resumePush(this);
        JPushInterface.setTags(getApplicationContext(), tags, null);
        JPushInterface.setAlias(getApplicationContext(), userInfo.userId,
                null);
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(userInfo.account);

        // en为英文版，取值zh为中文版。
        AppApplication.changeAppLanguage(getResources(), userInfo.language);
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
     * 功能描述: 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        // 获得手机分辨率
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 功能描述: 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        // 获得手机分辨率
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 功能描述: 获取屏幕级别
     */
    public void getScreenLevel() {
        int screenWidthPx = getScreenWidth(getApplicationContext());
        //适配240 320 480 屏幕
        if (screenWidthPx >= 240 && screenWidthPx < 720 - 100) {
            screenlg = "sm";
            //适配中等密度 720
        } else if (screenWidthPx >= 720 - 100 && screenWidthPx < 1080 - 100) {
            screenlg = "md";
            //适配1080
        } else if (screenWidthPx >= 1080 - 100) {
            screenlg = "lg";
            // 默认给定中屏尺寸
        } else {
            screenlg = "md";
        }
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
