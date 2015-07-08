package com.badou.mworking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.MainGridActivity;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.presenter.MainPresenter;

import org.json.JSONException;

import java.util.Calendar;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPush";
    private static final String KEY_TYPE = "type";
    private static final String TYPE_EXAM = "exam";
    private static final String TYPE_NOTICE = "notice";
    private static final String TYPE_TRAIN = "training";
    private static final String TYPE_TASK = "task";
    private static final String TYPE_CHAT = "chat";  //聊天
    private static final String TYPE_TONGSHIQUAN = "post";  //同事圈
    public static final String TYPE_ADD = "add"; //被评论的资源ad

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
/*            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
                    + ", extras: " + printBundle(bundle));*/

            if (JPushInterface.ACTION_REGISTRATION_ID
                    .equals(intent.getAction())) {
/*                String regId = bundle
                        .getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                System.out.println("[MyReceiver] 接收Registration Id : " + regId);*/
                // send the Registration Id to your server...
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                    .getAction())) {
/*                Log.d(TAG,
                        "[MyReceiver] 接收到推送下来的自定义消息: "
                                + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                System.out.println("[MyReceiver] 接收到推送下来的自定义消息: "
                        + bundle.getString(JPushInterface.EXTRA_MESSAGE));*/

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED
                    .equals(intent.getAction())) {
/*                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle
                        .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);*/
                processCustomMessage(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                    .getAction())) {
/*                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
                JPushInterface.reportNotificationOpened(context,
                        bundle.getString(JPushInterface.EXTRA_MSG_ID));*/
                toMessageCenter(context, bundle);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
                    .getAction())) {
/*                Log.d(TAG,
                        "[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
                                + bundle.getString(JPushInterface.EXTRA_EXTRA));
                // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
                // 打开一个网页等..
                System.out.println("[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
                        + bundle.getString(JPushInterface.EXTRA_EXTRA));*/
            } else {
/*                Log.d(TAG,
                        "[MyReceiver] Unhandled intent - " + intent.getAction());
                System.out.println("[MyReceiver] Unhandled intent - "
                        + intent.getAction());*/
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        MessageCenter messageCenter = null;
        try {
            messageCenter = new MessageCenter(bundle, Calendar.getInstance().getTimeInMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (messageCenter != null) {
            MessageCenterResManager.insertItem(context, messageCenter);
            Intent intent = new Intent(MainPresenter.ACTION_RECEIVER_MESSAGE);
            context.sendBroadcast(intent);
        }
    }

    private void toMessageCenter(Context context, Bundle bundle) {
        Intent intent = MainGridActivity.getIntent(context, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}
