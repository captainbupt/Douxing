package com.badou.mworking.base;

import android.app.Activity;
import android.app.ActivityManager;
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
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.ResourceHelper;
import com.badou.mworking.util.SPHelper;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChat;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
    public static boolean isInitialized = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initial(this);
        isInitialized = true;
    }

    public static void initial(AppApplication appApplication) {
        // Bitmap初始化必须在MyVolley之前，否则会丢出异常
        BitmapLruCache.init(appApplication);
        MyVolley.init(appApplication);
        //获取程序版本
        try {
            appVersion = getVersionName(appApplication);
        } catch (Exception e) {
            appVersion = "1.0";
            e.printStackTrace();
        }

        ResourceHelper.init(appApplication);
        SPHelper.initialize(appApplication);

        MTrainingDBHelper.init(appApplication);
        JPushInterface.setDebugMode(false);
        JPushInterface.init(appApplication);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(appApplication);
        initEMChat(appApplication, true);
    }

    private static void initEMChat(AppApplication appApplication, boolean isDebug) {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(appApplication, pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase("com.badou.mworking")) {
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        EMChat.getInstance().init(appApplication);
        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(isDebug);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
        EMChatEntity.init(appApplication);
    }

    private static String getAppName(AppApplication appApplication, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appApplication.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appApplication.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private static String getVersionName(AppApplication appApplication) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = appApplication.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(appApplication.getPackageName(), 0);
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
                        UserInfo.clearUserInfo((AppApplication) context.getApplicationContext());
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }).show();
    }
}
