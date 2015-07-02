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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.CrashHandler;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.domain.User;
import com.umeng.analytics.MobclickAgent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 程序入口application类
 */
public class AppApplication extends Application {

    // tag
    public static String appVersion;
    public static final String SYSVERSION = android.os.Build.VERSION.RELEASE;
    private UserInfo userInfo;

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
        initEMChat(true);
    }

    private void initEMChat(boolean isDebug) {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase("com.badou.mworking")) {
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        EMChat.getInstance().init(this);
        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(isDebug);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
        hxSDKHelper.onInit(this);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
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

    /**
     * 功能描述:  获取用户信息
     *
     * @return 用户信息类的对象实体
     */
    public UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = UserInfo.getUserInfo(this);
        }
        return userInfo;
    }

    /**
     * 功能描述:  设置保存用户对象信息
     *
     * @param userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
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

        com.easemob.chatuidemo.activity.LoginActivity.login(this, userInfo.account, userInfo.emchatPassword);
    }

    /**
     * 功能描述:  清除用户信息
     */
    public void clearUserInfo() {
        UserInfo.clearUserData(getApplicationContext());
        userInfo = null;
        JPushInterface.stopPush(this);
        EMChatManager.getInstance().logout();
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
                        ((AppApplication) context.getApplicationContext()).clearUserInfo();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }).show();
    }


    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        return hxSDKHelper.getContactList();
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxSDKHelper.setContactList(contactList);
    }

    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(emCallBack);
    }

}
