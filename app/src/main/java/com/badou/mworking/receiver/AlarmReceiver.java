package com.badou.mworking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.ExamActivity;
import com.badou.mworking.TrainActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.Train;
import com.badou.mworking.util.NotificationUtil;
import com.badou.mworking.util.SP;

/**
 * 功能描述: 定时器广播
 * 逻辑：
 * 优先级别 ：      考试 > 签到 > 培训 > 通知
 * 20150319 修改  定时8点提醒 只有  考试和培训         优先级     考试 > 培训
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_A = "alarmreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_A.equals(intent.getAction())) {
            notificationRemind(context);
        }
    }

    /**
     * 功能描述: 通知栏提醒
     *
     * @param context
     */
    private void notificationRemind(Context context) {
        NotificationUtil notificationUtil = new NotificationUtil();
        if (getUnreadNum(context, Exam.CATEGORY_KEY_UNREAD_NUM)) {
            notificationUtil.showNotification(context, ExamActivity.class, "您有未完成的考试");
        } else if (getUnreadNum(context, Train.CATEGORY_KEY_UNREAD_NUM)) {
            notificationUtil.showNotification(context, TrainActivity.class, "您有未读的课件");
        } else {
            return;
        }
    }

    /**
     * 功能描述:  获取缓存
     */
    private boolean getUnreadNum(Context context, String key) {
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        int unreadNum = SP.getIntSP(context, SP.DEFAULTCACHE, userNum + key, 0);
        if (unreadNum <= 0) {
            return false;
        }
        return false;
    }

}
