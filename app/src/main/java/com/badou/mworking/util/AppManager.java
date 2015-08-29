package com.badou.mworking.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Stack;

/**
 * 功能描述: Activity管理类
 */
public class AppManager {
	
	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager(){
	}

	/**
	 * 功能描述:获取AppManager对象，单例模式
	 * @return
	 */
	public static AppManager getAppManager(){
		if (instance == null){
			instance = new AppManager();
		}
		return instance;
	}
	
	/**
	 * 功能描述: 添加Activity到堆栈
	 * @param activity
	 */
	public void addActivity(Activity activity){
		if (activityStack == null){
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 功能描述: 获取当前Activity（堆栈中最后一个压入的）
	 * @return
	 */
	public Activity currentActivity(){
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 功能描述: 移除指定的Activity
	 * @param activity
	 */
	public void removeActivity(Activity activity){
		if (activity != null){
			activityStack.remove(activity);
		}
	}

	/**
	 * 功能描述: 结束所有Activity
	 */
	public static void finishAllActivity(){
		//这里可能会报数组越界的错误提前异常捕获
		Stack<Activity> activityStackTemp = new Stack<Activity>();
		activityStackTemp.addAll(activityStack);
		try {
			for (int i = 0, size = activityStackTemp.size(); i < size; i++){
				if (null != activityStackTemp.get(i)){
					if(!activityStackTemp.get(i).isFinishing()){
						activityStackTemp.get(i).finish();
					}
				}
			}
			activityStack.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述: 退出应用程序
	 * @param context 上下文
	 * @param isBackground 是否开启后台运行
	 */
	public void AppExit(Context context, Boolean isBackground){
		try{
			// 保存友盟数据
			MobclickAgent.onKillProcess(context);
			finishAllActivity();
/*
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.killBackgroundProcesses(context.getPackageName());
*/
//			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			// 注意，如果您有后台程序运行，请不要支持此句子
			if (!isBackground){
//				System.exit(0);    // 程序在下载时开启了下载service，此处不要杀掉任何进程
			}
		}
	}
}
