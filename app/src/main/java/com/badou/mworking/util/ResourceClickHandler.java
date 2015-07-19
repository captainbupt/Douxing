package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.ChatListActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.EntryActivity;
import com.badou.mworking.NoticeBaseActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskSignActivity;
import com.badou.mworking.TrainBaseActivity;
import com.badou.mworking.domain.MarkReadUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Chatter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ResourceClickHandler {

    public interface OnCompleteListener {
        void onComplete(boolean isSuccess);
    }

    public static void toChatterPage(final Context context, final String sid, final OnCompleteListener onCompleteListener) {
        ServiceProvider.doGetChatterById(context, sid, new VolleyListener(context) {
            @Override
            public void onResponseSuccess(JSONObject response) {
                Chatter chatter = new Chatter(response.optJSONObject(Net.DATA));
                Intent intent = new Intent(context, ChatterDetailActivity.class);
                intent.putExtra(ChatterDetailActivity.KEY_CHATTER, chatter);
                context.startActivity(intent);
                onCompleteListener.onComplete(true);
            }

            @Override
            public void onErrorCode(int code) {
                onCompleteListener.onComplete(false);
            }

        });
    }

    public static void toAskPage(final Context context, final String sid, final OnCompleteListener onCompleteListener) {
        ServiceProvider.doGetAskById(context, sid, new VolleyListener(context) {
            @Override
            public void onResponseSuccess(JSONObject response) {
                Ask ask = new Ask(response.optJSONObject(Net.DATA));
                Intent intent = new Intent(context, AskDetailActivity.class);
                intent.putExtra(AskDetailActivity.KEY_ASK, ask);
                context.startActivity(intent);
                onCompleteListener.onComplete(true);
            }

            @Override
            public void onErrorCode(int code) {
                onCompleteListener.onComplete(false);
            }
        });
    }

    public static void toChatPage(final Context context, final OnCompleteListener onCompleteListener) {
        onCompleteListener.onComplete(true);
        Intent intent = new Intent(context, ChatListActivity.class);
        UserDetail userDetail = null;
        try {
            JSONObject jsonObject = new JSONObject(SP.getStringSP(context, SP.DEFAULTCACHE, "userdetail", ""));
            userDetail = new UserDetail(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (userDetail != null) {
            intent.putExtra(ChatListActivity.KEY_HEAD_URL, userDetail.headimg);
        }
        context.startActivity(intent);
    }
}
