/* 
 * 文件名: NotificationUtil.java
 * 包路径: com.badou.mworking.util
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月9日 下午7:32:01
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月9日 下午7:32:01 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.R;

/**
 * 类: <code> NotificationUtil </code> 功能描述: Notification 工具类 创建人: 葛建锋 创建日期:
 * 2015年1月9日 下午7:32:01 开发环境: JDK7.0
 */
public class NotificationUtil {

	// BASE Notification ID
	private int Notification_ID_BASE = 110;

	public void showNotification(Context context,Class<?> clazz,String content) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(context, clazz);
		PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
		Notification baseNF = new Notification();  // 新建状态栏通知
		// 设置通知在状态栏显示的图标
		baseNF.icon = R.drawable.ic_launcher;
		// 通知时在状态栏显示的内容
		baseNF.tickerText = content;
		// 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
		// 如果要全部采用默认值, 用 DEFAULT_ALL.
		// 此处采用默认声音
		baseNF.defaults |= Notification.DEFAULT_SOUND;
		baseNF.defaults |= Notification.DEFAULT_VIBRATE;
		baseNF.defaults |= Notification.DEFAULT_LIGHTS;

		// FLAG_INSISTENT 让声音、振动无限循环，直到用户响应
		// 下面采用系统默认声音
		baseNF.flags |= Notification.DEFAULT_SOUND;

		// 通知被点击后，自动消失
		baseNF.flags |= Notification.FLAG_AUTO_CANCEL;

		// 点击'Clear'时，不清楚该通知(QQ的通知无法清除，就是用的这个)
		baseNF.flags |= Notification.FLAG_NO_CLEAR;
		// 第二个参数 ：下拉状态栏时显示的消息标题 expanded message title
		// 第三个参数：下拉状态栏时显示的消息内容 expanded message text
		// 第四个参数：点击该通知时执行页面跳转
		baseNF.setLatestEventInfo(context, "兜行", content, pd);
		// 发出状态栏通知
		// The first parameter is the unique ID for the Notification
		// and the second is the Notification object.
		nm.notify(Notification_ID_BASE, baseNF);

	}
}
