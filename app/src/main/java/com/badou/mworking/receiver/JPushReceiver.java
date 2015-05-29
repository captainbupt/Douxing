package com.badou.mworking.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.badou.mworking.AroundDetailActivity;
import com.badou.mworking.AroundUserActivity;
import com.badou.mworking.ChatListActivity;
import com.badou.mworking.ExamActivity;
import com.badou.mworking.NoticeActivity;
import com.badou.mworking.TaskActivity;
import com.badou.mworking.TrainActivity;
import com.badou.mworking.util.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

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
	public static final String TYPE_ADD = "add" ; //被评论的资源ad

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
					+ ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID
					.equals(intent.getAction())) {
				String regId = bundle
						.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				System.out.println("[MyReceiver] 接收Registration Id : " + regId);
				// send the Registration Id to your server...
			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
					.getAction())) {
				Log.d(TAG,
						"[MyReceiver] 接收到推送下来的自定义消息: "
								+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
				System.out.println("[MyReceiver] 接收到推送下来的自定义消息: "
						+ bundle.getString(JPushInterface.EXTRA_MESSAGE));

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED
					.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle
						.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
					.getAction())) {
				Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
				JPushInterface.reportNotificationOpened(context,
						bundle.getString(JPushInterface.EXTRA_MSG_ID));
				processCustomMessage(context, bundle);
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
					.getAction())) {
				Log.d(TAG,
						"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
								+ bundle.getString(JPushInterface.EXTRA_EXTRA));
				// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
				// 打开一个网页等..
				System.out.println("[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
						+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			} else {
				Log.d(TAG,
						"[MyReceiver] Unhandled intent - " + intent.getAction());
				System.out.println("[MyReceiver] Unhandled intent - "
						+ intent.getAction());
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
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				String type = extraJson.getString(KEY_TYPE);
				if (type != null) {
					// 在线考试
					if (type.equals(TYPE_EXAM)) {
						AppManager.finishActivity(ExamActivity.class);
						Intent examIntent = new Intent(context,ExamActivity.class);
						examIntent.putExtras(bundle);
						examIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(examIntent);
					// 通知公告
					} else if (type.equals(TYPE_NOTICE)) {
						Intent noticeIntent = new Intent(context,NoticeActivity.class);
						noticeIntent.putExtras(bundle);
						noticeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(noticeIntent);
					//微培训
					} else if (type.equals(TYPE_TRAIN)) {
						Intent trainIntent = new Intent(context,TrainActivity.class);
						trainIntent.putExtras(bundle);
						trainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(trainIntent);
					// 任务签到
					}else if (type.equals(TYPE_TASK)){
						Intent taskIntent = new Intent(context,TaskActivity.class);
						taskIntent.putExtras(bundle);
						taskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(taskIntent);
					}else if (type.equals(TYPE_CHAT)){
						Intent intent1 = new Intent(context, ChatListActivity.class);
						intent1.putExtras(bundle);
						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent1);
					}else if (type.equals(TYPE_TONGSHIQUAN)){
						AppManager.finishActivity(AroundDetailActivity.class);
						Intent intent2 = new Intent(context, AroundUserActivity.class);
						intent2.putExtras(bundle);
						intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent2);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
