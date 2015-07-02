package com.badou.mworking.model.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.badou.mworking.net.ResponseParameters;
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
    public String emchatPassword;

    /**
     * 功能描述:保存用户信息到sp中    但企业名称没有保存
     *
     * @param mContext
     */
    public void saveUserInfo(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ResponseParameters.USER_ACCOUNT, account);
        editor.putString(ResponseParameters.USER_ID, userId);
        editor.putInt(ResponseParameters.USER_ACCESS, access);
        editor.putString(ResponseParameters.USER_TAG, tag);
        editor.putString(ResponseParameters.USER_NAME, name);
        editor.putString(ResponseParameters.USER_DESCRIPTION, description);
        editor.putBoolean(ResponseParameters.USER_ADMIN, isAdmin);
        editor.putString(ResponseParameters.USER_SHUFFLE, shuffleStr.toString());
        editor.putString(ResponseParameters.USER_LANGUAGE, language);
        editor.putString(ResponseParameters.USER_COMPANY, company);
        editor.putString(ResponseParameters.USER_HOST, host);
        editor.putString("hxpwd", emchatPassword);
        editor.commit();
    }

    public static void clearUserData(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(ResponseParameters.USER_ID);
        editor.remove(ResponseParameters.USER_ACCESS);
        editor.remove(ResponseParameters.USER_TAG);
        editor.remove(ResponseParameters.USER_NAME);
        editor.remove(ResponseParameters.USER_DESCRIPTION);
        editor.remove(ResponseParameters.USER_ADMIN);
        editor.remove(ResponseParameters.USER_SHUFFLE);
        editor.remove(ResponseParameters.USER_LANGUAGE);
        editor.remove(ResponseParameters.USER_COMPANY);
        editor.remove(ResponseParameters.USER_HOST);
        editor.remove("hxpwd");
        editor.commit();
    }

    public static UserInfo getUserInfo(Context mContext) {
        String userId = SP.getStringSP(mContext, SP.DEFAULTCACHE, ResponseParameters.USER_ID, "");
        String account = SP.getStringSP(mContext, SP.DEFAULTCACHE, ResponseParameters.USER_ACCOUNT, "");
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(account)) {
            return null;
        }
        SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
                Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();
        userInfo.account = account;
        userInfo.userId = userId;
        userInfo.access = sp.getInt(ResponseParameters.USER_ACCESS, 0);
        userInfo.tag = sp.getString(ResponseParameters.USER_TAG, "");
        userInfo.name = sp.getString(ResponseParameters.USER_NAME, "");
        userInfo.description = sp.getString(ResponseParameters.USER_DESCRIPTION, "");
        userInfo.isAdmin = sp.getBoolean(ResponseParameters.USER_ADMIN, false);
        try {
            userInfo.shuffleStr = new JSONObject(sp.getString(ResponseParameters.USER_SHUFFLE, ""));
        } catch (JSONException e) {
            userInfo.shuffleStr = new JSONObject();
        }
        userInfo.language = sp.getString(ResponseParameters.USER_LANGUAGE, "");
        userInfo.company = sp.getString(ResponseParameters.USER_COMPANY, "");
        userInfo.host = sp.getString(ResponseParameters.USER_HOST, "");
        userInfo.emchatPassword = sp.getString("hxpwd", "");
        return userInfo;
    }

    public void setUserInfo(String account, JSONObject jsonObject) {
        this.account = account;
        if (jsonObject != null) {
            this.userId = jsonObject.optString(ResponseParameters.USER_ID);
            this.access = jsonObject.optInt(ResponseParameters.USER_ACCESS);
            this.tag = jsonObject.optString(ResponseParameters.USER_TAG);
            this.name = jsonObject.optString(ResponseParameters.USER_NAME);
            this.description = jsonObject.optString(ResponseParameters.USER_DESCRIPTION);
            this.isAdmin = jsonObject.optInt(ResponseParameters.USER_ADMIN) == 1 ? true : false;
            this.shuffleStr = jsonObject.optJSONObject(ResponseParameters.USER_SHUFFLE);
            this.language = jsonObject.optString(ResponseParameters.USER_LANGUAGE);
            this.company = jsonObject.optString(ResponseParameters.USER_COMPANY);
            this.host = jsonObject.optString(ResponseParameters.USER_HOST);
            this.emchatPassword = jsonObject.optString("hxpwd");
        }
    }
}
