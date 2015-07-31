package com.badou.mworking.entity.emchat;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.domain.User;

import java.util.Map;

public class EMChatEntity {

    public static EMChatEntity getInstance() {
        return emChatEntity;
    }

    private static EMChatEntity emChatEntity;

    // login user name
    public final String PREF_USERNAME = "username";
 
    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public String currentUserNick = "";
    public DemoHXSDKHelper hxSDKHelper;

    public EMChatEntity(Application application) {
        hxSDKHelper = new DemoHXSDKHelper();
        hxSDKHelper.onInit(application);
    }

    public static void init(Application application) {
        emChatEntity = new EMChatEntity(application);
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        return hxSDKHelper.getContactList();
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxSDKHelper.setContactList(contactList);
    }

    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     */
    public void setUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(emCallBack);
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        try {
            User user = getInstance().getContactList().get(username);
            if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
                String imgUrl = user.getAvatar();
                ImageViewLoader.setSquareImageViewResource(imageView, R.drawable.icon_emchat_single, imgUrl, context.getResources().getDimensionPixelSize(R.dimen.icon_size_medium));
            } else {
                imageView.setImageResource(R.drawable.icon_emchat_single);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.icon_emchat_single);
        }
    }

    public static String getUserNick(String username) {
        if (username.equals(ChatActivity.SERVICE_ACCOUNT)) {
            return "兜行客服";
        }
        User user = EMChatEntity.getInstance().getContactList().get(username);
        if (user == null) {
            return username;
        } else {
            return user.getNick();
        }
    }

}
