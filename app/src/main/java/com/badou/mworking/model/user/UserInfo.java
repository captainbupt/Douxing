package com.badou.mworking.model.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.SP;

import org.json.JSONObject;

public class UserInfo{
	
	private String userId;      //用户id
	private int access;			// 模块权限
	private String tag;
	private String name;
	private String desc;
	private boolean isAdmin = false; 	// 是否是管理员         (其中 1 代表管理员，  0 代表非管理员)
	private String userNumber;

	public static final String USER_NUMBER = "userNumber";//用户账户 
	public static final String USER_ID = "userId";
	public static final String USER_ACCESS = "userAccess";
	public static final String USER_TAG = "userTag";
	public static final String USER_NAME = "userName";//用户名
	public static final String USER_DESC = "userDesc";
	public static final String USER_ADMIN = "isAdmin"; // 是否是管理员

	/**
	 * 功能描述:保存用户信息到sp中    但企业名称没有保存
	 * @param mContext
	 */
	public void saveUserInfo(Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USER_NUMBER,userNumber);
		editor.putString(USER_ID, userId);
		editor.putInt(USER_ACCESS, access);
		editor.putString(USER_TAG, tag);
		editor.putString(USER_NAME, name);
		editor.putString(USER_DESC, desc);
		editor.putBoolean(USER_ADMIN, isAdmin);
		editor.commit();
	}

	public static void clearUserData(Context mContext) {
		SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(USER_ID, "");
		editor.putInt(USER_ACCESS, -1);
		editor.putString(USER_TAG, "");
		editor.putString(USER_NAME, "");
		editor.putString(USER_DESC, "");
		editor.putBoolean(USER_ADMIN, false);
		editor.commit();
	}

	public static UserInfo getUserInfo(Context mContext) {
		String userId = SP.getStringSP(mContext, SP.DEFAULTCACHE,USER_ID, "");
		String userNum = SP.getStringSP(mContext, SP.DEFAULTCACHE,USER_NUMBER, "");
		if (TextUtils.isEmpty(userId)) {
			return null;
		}
		SharedPreferences sp = mContext.getSharedPreferences(SP.DEFAULTCACHE,
				Context.MODE_PRIVATE);
		UserInfo userInfo = new UserInfo();
		userInfo.setUserNumber(userNum);
		userInfo.setUserId(userId);
		userInfo.setAccess(sp.getInt(USER_ACCESS, 0));
		userInfo.setTag(sp.getString(USER_TAG, ""));
		userInfo.setName(sp.getString(USER_NAME, ""));
		userInfo.setDesc(sp.getString(USER_DESC, ""));
		userInfo.setAdmin(sp.getBoolean(USER_ADMIN, false));
		return userInfo;
	}
	
	public void setUserInfo(String userNumber, JSONObject jsonObject) {
		this.userNumber = userNumber;
		if (jsonObject != null) {
			this.userId = jsonObject.optString(ResponseParams.USER_ID);
			this.access = jsonObject.optInt(ResponseParams.USER_ACCESS);
			this.tag = jsonObject.optString(ResponseParams.USER_TAG);
			this.name = jsonObject.optString(ResponseParams.USER_NAME);
			this.desc = jsonObject.optString(ResponseParams.USER_DESC);
			int admin = jsonObject.optInt("admin");
			if(1 == admin){
				this.isAdmin = true;
			}else{
				this.isAdmin = false;
			}
		}
	}

	public void setUserInfoChange(JSONObject jsonObject) {
		if (jsonObject != null) {
			this.userId = jsonObject.optString(ResponseParams.USER_ID);
			this.access = jsonObject.optInt(ResponseParams.USER_ACCESS);
			this.tag = jsonObject.optString(ResponseParams.USER_TAG);
		}
	}
	
	
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getName() {
		if (name == null || name.equals("null"))
			return "";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		if (desc == null || desc.equals("null"))
			return "";
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
