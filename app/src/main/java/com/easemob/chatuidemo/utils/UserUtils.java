package com.easemob.chatuidemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.easemob.chatuidemo.domain.User;
import com.squareup.picasso.Picasso;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = AppApplication.getInstance().getContactList().get(username);
        if (user == null) {
            user = new User();
        }
        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.icon_emchat_single).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.icon_emchat_single).into(imageView);
        }
    }

}
