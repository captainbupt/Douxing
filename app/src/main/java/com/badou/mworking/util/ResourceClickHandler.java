package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONObject;

public class ResourceClickHandler {

    public interface OnCompleteListener {
        void onComplete(boolean isSuccess);
    }

    public static void toChatterPage(final Context context, final String sid, final OnCompleteListener onCompleteListener) {
        Intent intent = ChatterDetailActivity.getIntent(context, sid);
        context.startActivity(intent);
        onCompleteListener.onComplete(true);
    }

    public static void toAskPage(final Context context, final String sid, final OnCompleteListener onCompleteListener) {
        context.startActivity(AskDetailActivity.getIntent(context, sid));
        onCompleteListener.onComplete(true);
    }

/*    public static void toChatPage(final Context context, final OnCompleteListener onCompleteListener) {
        onCompleteListener.onComplete(true);
        Intent intent = new Intent(context, ChatListActivity.class);
        UserDetail userDetail = null;
        userDetail = SPHelper.getUserDetail();
        if (userDetail != null) {
            intent.putExtra(ChatListActivity.KEY_HEAD_URL, userDetail.getHeadimg());
        }
        context.startActivity(intent);
    }*/
}
