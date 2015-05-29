package com.badou.mworking.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 功能描述: 聊天列表实体类
 */
public class ChattingListInfo {

	public String img;      //联系人头像url
	public String content;   //联系人列表显示说过的话
	public long ts;          //时间
	public String own;      //我的电话号码
	public String whom;     // 聊天人的电话号码
	public int msgcnt;    //信息条数
	public String name;      //聊天人的姓名
	
	public ChattingListInfo(JSONObject dataJson) {
		this.whom = dataJson.optString("whom");
		this.name = dataJson.optString("name");
		this.img = dataJson.optString("img");
		this.msgcnt = dataJson.optInt("msgcnt");
		JSONObject msginfo = dataJson.optJSONObject("msginfo");
		if(msginfo!= null){
			this.content = msginfo.optString("content");
			this.ts = msginfo.optLong("ts");
			this.own = msginfo.optString("own");
		}
	}
}
