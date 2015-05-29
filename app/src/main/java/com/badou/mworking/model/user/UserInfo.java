package com.badou.mworking.model.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {

    public String userId;      //用户id
    public int access;            // 模块权限
    public String tag;
    public String name;
    public String description;
    public boolean isAdmin = false;    // 是否是管理员         (其中 1 代表管理员，  0 代表非管理员)
    public String account;
    public String company;
    public String language;
    public JSONObject shuffleStr;
    public String host;

    /**
     * 功能描述:保存用户信息到sp中    但企业名称没有保存
     *
     * @param mContext
     */
    public void saveUserInfo(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ResponseParams.USER_ACCOUNT, account);
        editor.putString(ResponseParams.USER_ID, userId);
        editor.putInt(ResponseParams.USER_ACCESS, access);
        editor.putString(ResponseParams.USER_TAG, tag);
        editor.putString(ResponseParams.USER_NAME, name);
        editor.putString(ResponseParams.USER_DESCRIPTION, description);
        editor.putBoolean(ResponseParams.USER_ADMIN, isAdmin);
        editor.putString(ResponseParams.USER_SHUFFLE, shuffleStr.toString());
        editor.putString(ResponseParams.USER_LANGUAGE, language);
        editor.putString(ResponseParams.USER_COMPANY, company);
        editor.putString(ResponseParams.USER_HOST, host);
        editor.commit();
    }

    public static void clearUserData(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(ResponseParams.USER_ID);
        editor.remove(ResponseParams.USER_ACCESS);
        editor.remove(ResponseParams.USER_TAG);
        editor.remove(ResponseParams.USER_NAME);
        editor.remove(ResponseParams.USER_DESCRIPTION);
        editor.remove(ResponseParams.USER_ADMIN);
        editor.remove(ResponseParams.USER_SHUFFLE);
        editor.remove(ResponseParams.USER_LANGUAGE);
        editor.remove(ResponseParams.USER_COMPANY);
        editor.remove(ResponseParams.USER_HOST);
        editor.commit();
    }

    public static UserInfo getUserInfo(Context mContext) {
        String userId = SP.getStringSP(mContext, SP.DEFAULTCACHE, ResponseParams.USER_ID, "");
        String account = SP.getStringSP(mContext, SP.DEFAULTCACHE, ResponseParams.USER_ACCOUNT, "");
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(account)) {
            return null;
        }
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();
        userInfo.account = account;
        userInfo.userId = userId;
        userInfo.access = sp.getInt(ResponseParams.USER_ACCESS, 0);
        userInfo.tag = sp.getString(ResponseParams.USER_TAG, "");
        userInfo.name = sp.getString(ResponseParams.USER_NAME, "");
        userInfo.description = sp.getString(ResponseParams.USER_DESCRIPTION, "");
        userInfo.isAdmin = sp.getBoolean(ResponseParams.USER_ADMIN, false);
        try {
            userInfo.shuffleStr = new JSONObject(sp.getString(ResponseParams.USER_SHUFFLE, ""));
        } catch (JSONException e) {
            userInfo.shuffleStr = new JSONObject();
        }
        userInfo.language = sp.getString(ResponseParams.USER_LANGUAGE, "");
        userInfo.company = sp.getString(ResponseParams.USER_COMPANY, "");
        userInfo.host = sp.getString(ResponseParams.USER_HOST, "");
        return userInfo;
    }

    public void setUserInfo(String account, JSONObject jsonObject) {
        this.account = account;
        if (jsonObject != null) {
            this.userId = jsonObject.optString(ResponseParams.USER_ID);
            this.access = jsonObject.optInt(ResponseParams.USER_ACCESS);
            this.tag = jsonObject.optString(ResponseParams.USER_TAG);
            this.name = jsonObject.optString(ResponseParams.USER_NAME);
            this.description = jsonObject.optString(ResponseParams.USER_DESCRIPTION);
            this.isAdmin = jsonObject.optInt(ResponseParams.USER_ADMIN) == 1 ? true : false;
            this.shuffleStr = jsonObject.optJSONObject(ResponseParams.USER_SHUFFLE);
            this.language = jsonObject.optString(ResponseParams.USER_LANGUAGE);
            this.company = jsonObject.optString(ResponseParams.USER_COMPANY);
            this.host = jsonObject.optString(ResponseParams.USER_HOST);
        }
    }
}
