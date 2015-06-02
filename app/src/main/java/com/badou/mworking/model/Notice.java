package com.badou.mworking.model;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

/**
 * 功能描述: 通知实体类
 */
public class Notice extends Category{

	public static final int CATEGORY_TYPE = Category.CATEGORY_NOTICE;
	public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
	public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//通知 的 未读数量

	public String imgUrl;

	/**
	 * 功能描述: 通知公告json解析
	 * @param jsonObject
	 */
	public Notice(JSONObject jsonObject) {
		super(jsonObject);
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
}
