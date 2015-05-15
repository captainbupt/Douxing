package com.badou.mworking.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 类:  <code> NetUtils </code>
 * 功能描述: 网络工具类
 * 创建人:  葛建锋
 * 创建日期: 2014年7月15日 下午3:50:44
 * 开发环境: JDK7.0
 */
public class NetUtils {
	
	/**
	 * 功能描述:  判断是否联网
	 * @param context  上下文对象
	 * @return 联网true  没联网false
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}
		return true;
	}

	/**
	 * 功能描述:  是否wifi连接
	 * @param context     上下文对象
	 * @return 是 true 否false
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
}
