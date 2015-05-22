package com.badou.mworking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.ExamActivity;
import com.badou.mworking.TrainActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.Train;
import com.badou.mworking.util.NotificationUtil;

/**
 * 功能描述: 定时器广播
 * 逻辑：   
 * 		优先级别 ：      考试 > 签到 > 培训 > 通知
 * 			20150319 修改  定时8点提醒 只有  考试和培训         优先级     考试 > 培训
 * 

 */
public class AlarmReceiver extends BroadcastReceiver {

	public static final String ACTION_A="alarmreceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_A.equals(intent.getAction())) {
			notificationRemind(context);
		}
	}
	
	/**
	 * 功能描述: 通知栏提醒
	 * @param context
	 */
	private void notificationRemind(Context context){
		NotificationUtil notificationUtil = new NotificationUtil();
		if(Exam.getUnreadNum(context)){
			notificationUtil.showNotification(context,ExamActivity.class,"您有未完成的考试");
		}else if(Train.getUnreadNum(context)){
			notificationUtil.showNotification(context,TrainActivity.class,"您有未读的课件");
		}else{
			return;
		}
	}
}
