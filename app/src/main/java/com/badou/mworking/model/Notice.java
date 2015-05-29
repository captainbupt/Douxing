package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 通知实体类
 */
public class Notice extends Category{

	public static final int CATEGORY_TYPE = Category.CATEGORY_NOTICE;
	public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
	public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//通知 的 未读数量

	public int isRead = 0;

	public String imgUrl;

	/**
	 * 功能描述: 通知公告json解析
	 * @param jsonObject
	 */
	public Notice(JSONObject jsonObject) {
		super(jsonObject);
		this.isRead = jsonObject.optInt(RequestParams.NOTICE_READ);
		this.imgUrl = jsonObject
				.optString(MTrainingDBHelper.CHK_IMG);
	}

	@Override
	public int getCategoryType() {
		return CATEGORY_TYPE;
	}

	@Override
	public String getCategoryKeyName() {
		return CATEGORY_KEY_NAME;
	}

	@Override
	public String getCategoryKeyUnread() {
		return CATEGORY_KEY_UNREAD_NUM;
	}

	/**
	 * 功能描述:  获取缓存
	 */
	public static boolean getUnreadNum(Context context){
			String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
			String sp = SP.getStringSP(context,SP.DEFAULTCACHE, userNum+ CATEGORY_KEY_UNREAD_NUM, "");
			if(TextUtils.isEmpty(sp)){
				return false;
			}
			try {
				JSONArray resultArray = new JSONArray(sp);
				for (int i = 0 ; i < resultArray.length(); i++) {
					JSONObject jsonObject = resultArray
							.optJSONObject(i);
					Notice entity = new Notice(jsonObject);
					if(1 == entity.isRead){
						return true;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

}
