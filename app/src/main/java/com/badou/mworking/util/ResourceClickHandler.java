package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.ChatListActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.model.Ask;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.MessageCenter;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.widget.WaitProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/25 0025.
 */
public class ResourceClickHandler {

    public interface OnCompleteListener {
        void onComplete(boolean isSuccess);
    }

    public static void toCategoryPage(final Context context, final int type, final String rid, final String subject, final OnCompleteListener onCompleteListener) {
        ServiceProvider.getResourceDetail(context, rid, new VolleyListener(context) {
            @Override
            public void onResponseSuccess(JSONObject jsonObject) {
                onCompleteListener.onComplete(true);
                CategoryDetail detail = new CategoryDetail(context, jsonObject.optJSONObject(Net.DATA), type, rid, subject, null);
                CategoryClickHandler.categoryClicker(context, detail);
            }

            @Override
            public void onErrorCode(int code) {
                onCompleteListener.onComplete(false);
            }
        });
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
