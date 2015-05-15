package com.badou.mworking.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.UUID;

/**
 * 类:  <code> NetworkUtils </code>
 * 功能描述:  网络操作工具类
 * 创建人: 葛建锋
 * 创建日期: 2013-11-29 下午3:48:42
 * 开发环境: JDK6.0
 */
public class NetworkUtils {
	
	/**
	 * 功能描述: 判断当前网络是否可用,是否联网
	 * @param context
	 * @return true 网络已连接  false 网络未连接
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 功能描述:  从下载地址中获取下载APK文件的文件名称
	 * @param url   APK文件的下载地址
	 * @return  APK 文件的文件名
	 */
	public static String getFileNameFromUrl(String url) {
		// 通过 ‘？’ 和 ‘/’ 判断文件名
		int index = url.lastIndexOf('?');
		String filename;
		if (index > 1) {
			filename = url.substring(url.lastIndexOf('/') + 1, index);
		} else {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			filename = UUID.randomUUID() + ".apk";// 默认取一个文件名
		}
		return filename;
	}
}
