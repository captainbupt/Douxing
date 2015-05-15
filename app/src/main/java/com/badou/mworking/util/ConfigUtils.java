package com.badou.mworking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类:  <code> ConfigUtils </code>
 * 功能描述: 
 * 创建人: 葛建锋
 * 创建日期: 2013-11-29 下午1:44:58
 * 开发环境: JDK6.0
 */
public class ConfigUtils {

	public static final int URL_COUNT = 3; 
	public static final String KEY_URL = "url";
	
	public static final String KEY_RX_WIFI = "rx_wifi";
	public static final String KEY_TX_WIFI = "tx_wifi";
	public static final String KEY_RX_MOBILE = "tx_mobile";
	public static final String KEY_TX_MOBILE = "tx_mobile";
	public static final String KEY_Network_Operator_Name = "operator_name";
	
	public static final String PREFERENCE_NAME = "com.yyxu.download";

	/**
	 * 功能描述:
	 * @param context
	 * @return
	 */
	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_WORLD_WRITEABLE);
	}

	/**
	 * 功能描述:  获取SharedPreferences中的内容
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}
	
	/**
	 * 功能描述: SharedPreferences中保存内容
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setString(Context context, String key, String value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * 功能描述: 保存url
	 * @param context
	 * @param index
	 * @param url
	 */
	public static void storeURL(Context context, int index, String url) {
		setString(context, KEY_URL + index, url);
	}

	/**
	 * 功能描述: 清空url
	 * @param context
	 * @param index
	 */
	public static void clearURL(Context context, int index) {
		setString(context, KEY_URL + index, "");
	}

	/** 
	 * 功能描述:  获取url
	 * @param context
	 * @param index
	 * @return
	 */
	public static String getURL(Context context, int index) {
		return getString(context, KEY_URL + index);
	}

	/**
	 * 功能描述: 获取url的list集合
	 * @param context
	 * @return
	 */
	public static List<String> getURLArray(Context context) {
		List<String> urlList = new ArrayList<String>();
		for (int i = 0; i < URL_COUNT; i++) {
			if (!TextUtils.isEmpty(getURL(context, i))) {
				urlList.add(getString(context, KEY_URL + i));
			}
		}
		return urlList;
	}


}
