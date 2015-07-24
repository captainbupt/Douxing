/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.badou.mworking.MainGridActivity;
import com.badou.mworking.R;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.HXNotifier;
import com.easemob.applib.model.HXNotifier.HXNotificationInfoProvider;
import com.easemob.applib.model.HXSDKModel;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.MainActivity;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.receiver.CallReceiver;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 *
 * @author easemob
 */
public class DemoHXSDKHelper extends HXSDKHelper {

    private static final String TAG = "DemoHXSDKHelper";

    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    private CallReceiver callReceiver;

    /**
     * 用来记录foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    @Override
    protected void initHXOptions() {
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
    }

    @Override
    protected void initListener() {
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);
        //注册消息事件监听
        initEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                    // 实时更新群名称
                    if (!TextUtils.isEmpty(message.getStringAttribute("groupname", null))) {
                        String groupId = message.getTo();
                        EMGroupManager.getInstance().getGroup(groupId).setGroupName(message.getStringAttribute("groupname", ""));
                    }
                }
                switch (event.getEvent()) {
                    case EventNewMessage:
                        //应用在后台，不需要刷新UI,通知栏提示新消息
                        EMLog.d(TAG, "receive new message: " + activityList.size());
                        if (activityList.size() <= 0) {
                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        }
                        break;
                    case EventOfflineMessage:
                        EMLog.d(TAG, "received offline messages: " + activityList.size());
                        if (activityList.size() <= 0) {
                            List<EMMessage> messages = (List<EMMessage>) event.getData();
                            HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {

                        EMLog.d(TAG, "收到透传消息");
                        //获取消息body
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                        final String action = cmdMsgBody.action;//获取自定义action

                        //获取扩展属性 此处省略
                        //message.getStringAttribute("");
                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                        final String str = appContext.getString(R.string.receive_the_passthrough);

                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                        if (broadCastReceiver == null) {
                            broadCastReceiver = new BroadcastReceiver() {

                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                                }
                            };

                            //注册广播接收者
                            appContext.registerReceiver(broadCastReceiver, cmdFilter);
                        }

                        Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                        broadcastIntent.putExtra("cmd_value", str + action);
                        appContext.sendBroadcast(broadcastIntent, null);

                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;

            private void showToast(String value) {
                if (!registered) {
                    //注册广播接收者
                    appContext.registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(appContext, intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }

                    }, filter);

                    registered = true;
                }

                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info", "onChatRoomDestroyed=" + roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined=" + participant);

            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited=" + participant);

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked=" + participant);

            }

        });
    }

    /**
     * 自定义通知栏提示内容
     *
     * @return
     */
    @Override
    protected HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                return message.getFrom() + ": " + ticker;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                String id;
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // 单聊信息
                    id = message.getFrom();
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    id = message.getTo();
                }
                return MainGridActivity.getIntent(appContext, id, chatType == ChatType.Chat);
            }
        };
    }


    @Override
    protected void onConnectionConflict() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }

    @Override
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }


    @Override
    protected HXSDKModel createModel() {
        return new DemoHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier() {
        return new HXNotifier() {
            public synchronized void onNewMsg(final EMMessage message) {
                if (EMChatManager.getInstance().isSlientMessage(message)) {
                    return;
                }
                String chatUsename = message.getTo();
                // 获取设置的不提示新消息的用户或者群组ids
                List<String> notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledGroups();
                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }

                    viberateAndPlayTone(message);
                }
            }
        };
    }

    /**
     * get demo HX SDK Model
     */
    public DemoHXSDKModel getModel() {
        return (DemoHXSDKModel) hxModel;
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        /*if (getHXId() != null && contactList == null) {
            contactList = ((DemoHXSDKModel) getModel()).getContactList();
        }*/
        if (contactList == null) {
            contactList = getModel().getContactList();
        }

        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    @Override
    public void logout(final EMCallBack callback) {
        endCall();
        super.logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginAnonymous(final Activity activity, final EMCallBack emCallBack) {
        final String imei = ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        ServiceProvider.registerAccount(activity, imei, new VolleyListener(activity) {
            @Override
            public void onResponseSuccess(JSONObject response) {
                final String password = response.optJSONObject(Net.DATA).optString("hxpwd");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        loginEMChat(activity, imei, password, emCallBack);
                    }
                }.start();
            }

            @Override
            public void onErrorCode(int code) {
                super.onErrorCode(code);
            }
        });
    }

    /**
     * 登录
     */
    public void loginEMChat(final Activity activity, final String currentUsername, final String currentPassword, final EMCallBack emCallBack) {
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.showToast(activity, R.string.Login_failed);
            return;
        }
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            EMChatManager.getInstance().logout();
        }
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                EMChatEntity.getInstance().setUserName(currentUsername);
                EMChatEntity.getInstance().setPassword(currentPassword);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
/*                            EMGroupManager.getInstance().loadAllGroups();
                            EMChatManager.getInstance().loadAllConversations();*/
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    EMChatEntity.getInstance().logout(null);
                    Toast.makeText(activity, R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emCallBack.onSuccess();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                emCallBack.onProgress(progress, status);
            }

            @Override
            public void onError(final int code, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emCallBack.onError(code, message);
                    }
                });
            }
        });
    }

}