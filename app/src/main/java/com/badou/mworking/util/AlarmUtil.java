/* 
 * 文件名: AlarmUtils.java
 * 包路径: com.badou.mworking.util
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月9日 下午7:25:14
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月9日 下午7:25:14 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.receiver.AlarmReceiver;

import java.util.Calendar;

/**
 * 类:  <code> AlarmUtils </code>
 * 功能描述: 定时器工具类
 * 创建人:  葛建锋
 * 创建日期: 2015年1月9日 下午7:25:14
 * 开发环境: JDK7.0
 */
public class AlarmUtil {

	/**
	 * 功能描述: 开启定时器
	 * @param context
	 */
	public void OpenTimer(Context context){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 20);   // 设置20点（晚上8点提醒）
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(AlarmReceiver.ACTION_A);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager;
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),1000*60*60*24, pendingIntent);
	}
	
	public void cancel(Context context){
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(AlarmReceiver.ACTION_A);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
}
