/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
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
package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.badou.mworking.R;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;

/**
 * 登陆页面, 保留功能，不要页面
 */
public class LoginActivity extends BaseActivity {

    /**
     * 登录
     */
    public static void login(final Context context, final String currentUsername, final String currentPassword) {
        if (!DemoHXSDKHelper.getInstance().isLogined()) {
            // 调用sdk登陆方法登陆聊天服务器
            EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

                @Override
                public void onSuccess() {
                    // 登陆成功，保存用户名密码
                    AppApplication.getInstance().setUserName(currentUsername);
                    AppApplication.getInstance().setPassword(currentPassword);

                    try {
                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        // 处理好友和群组
                        initializeContacts(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 取好友或者群聊失败，不让进入主页面
                        AppApplication.getInstance().logout(null);
                        Toast.makeText(context.getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                    boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                            AppApplication.currentUserNick.trim());
                    if (!updatenick) {
                        Log.e("LoginActivity", "update current user nick fail");
                    }
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.Login_failed) + message,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static void initializeContacts(Context context) {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = context.getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = context.getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        AppApplication.getInstance().setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(context.getApplicationContext());
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }
}
