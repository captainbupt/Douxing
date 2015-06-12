package com.badou.mworking.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 功能描述:  SharedPerences保存数据和读取数据封装之后的工具类
 */
public class SP {

    // 请勿随意更改值，会导致之前版本缓存异常
    public static final String DEFAULTCACHE = "douxing";  //默认普通缓存
    public static final String CHATTER = "tongshiquan";  // 同事圈缓存
    public static final String CHATTERHOT = "tongshiquanhot";  // 同事圈缓存
    public static final String ASK = "wenda";  // 问答缓存


    // save float shared preference
    public static void putFloatSP(Context context, String fileName, String key, float value) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value).commit();
    }

    // save int shared preference
    public static void putIntSP(Context context, String fileName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value).commit();
    }

    // save long shared preference
    public static void putLongSP(Context context, String fileName, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value).commit();
    }

    // get float shared preference
    public static float getFloatSP(Context context, String fileName, String key,
                                   float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }

    // get int shared preference
    public static int getIntSP(Context context, String fileName, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        try {
            return sp.getInt(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    // get long shared preference
    public static long getLongSP(Context context, String fileName, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    // put boolean value
    public static void putBooleanSP(Context context, String fileName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value).commit();
    }

    // get boolean value
    public static boolean getBooleanSP(Context context, String fileName, String key,
                                       boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    // put string value
    public static void putStringSP(Context context, String fileName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value).commit();
    }

    // get string value
    public static String getStringSP(Context context, String fileName, String key,
                                     String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        String result;
        try {
            result = sp.getString(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
        return result;
    }

    public static boolean isContains(Context context, String fileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.contains(key.toString().trim());
    }

    public static void clearSP(Context context, String fileName) {
        try {
            SharedPreferences sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE);
            sp.edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
