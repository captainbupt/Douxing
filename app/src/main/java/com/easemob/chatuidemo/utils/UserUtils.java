package com.easemob.chatuidemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.MyVolley;
import com.easemob.chat.EMChatManager;
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
        try {
            User user = getUserInfo(username);
            if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
                String imgUrl = user.getAvatar();
                ImageViewLoader.setSquareImageViewResource(imageView, R.drawable.icon_emchat_single, imgUrl, context.getResources().getDimensionPixelSize(R.dimen.icon_size_medium));
            } else {
                imageView.setImageResource(R.drawable.icon_emchat_single);
            }
        }catch (Exception e){
            imageView.setImageResource(R.drawable.icon_emchat_single);
        }
    }

}
