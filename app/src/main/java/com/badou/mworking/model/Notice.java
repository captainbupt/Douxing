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

import java.io.Serializable;

/**
 * 类:  <code> Notice </code>
 * 功能描述: 通知实体类 
 * 创建人:  葛建锋
 * 创建日期: 2014年7月17日 下午4:51:01
 * 开发环境: JDK7.0
 */
public class Notice implements Serializable{
	
	public static final String CATEGORY_NOTICE = "notice"; 
	public static final String UNREAD_NUM_NOTICE = "noticeUnreadNum";//通知 的 未读数量
	
	private int top= 0;      // top 默认为0，1表示置顶
	private int tag;
	private int subtype = 0;
	private int isRead = 0;
	
	private long time;

	private String subject;
	private String department;
	private String url;
	private String rid;
	private String imgUrl;

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public int getSubType() {
		return subtype;
	}

	public void setSubType(int subtype) {
		this.subtype = subtype;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * 功能描述: 通知公告json解析
	 * @param jsonObject
	 */
	public Notice(JSONObject jsonObject) {
		super();
		this.rid = jsonObject.optString(ResponseParams.RESOURCE_ID);
		this.subject = jsonObject.optString(ResponseParams.NOTICE_SUBJECT);
		this.department = jsonObject
				.optString(ResponseParams.NOTICE_DEPARTMENT);
		this.time = jsonObject.optLong(ResponseParams.RESOURCE_TIME) * 1000;
		this.url = jsonObject.optString(ResponseParams.NOTICE_URL);
		this.subtype = jsonObject.optInt(ResponseParams.NOTICE_TYPE);
		this.isRead = jsonObject.optInt(RequestParams.NOTICE_READ);
		this.top = jsonObject.optInt(MTrainingDBHelper.TOP);
		this.imgUrl = jsonObject
				.optString(MTrainingDBHelper.CHK_IMG);
		this.tag = jsonObject
				.optInt(MTrainingDBHelper.NOTICE_TAG);
	}

	public Notice(String subject, String department, long time, String url,
			String rid, int type, int isRead,int top,String imgUrl,int tag) {
		super();
		this.rid = rid;
		this.subject = subject;
		this.department = department;
		this.time = time;
		this.url = url;
		this.subtype = type;
		this.isRead = isRead;
		this.top =top;
		this.imgUrl = imgUrl;
		this.tag = tag;
	}
	
	public static void putSPJsonArray(Context context, String tag,String userNum,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context, SP.NOTICE,userNum+tag, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.NOTICE, userNum+tag, SPJsonArray2.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能描述:  获取缓存
	 */
	public static boolean getUnreadNum(Context context){
			String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().getUserNumber();
			String sp = SP.getStringSP(context,SP.DEFAULTCACHE, userNum+Notice.UNREAD_NUM_NOTICE, "");
			if(TextUtils.isEmpty(sp)){
				return false;
			}
			try {
				JSONArray resultArray = new JSONArray(sp);
				for (int i = 0 ; i < resultArray.length(); i++) {
					JSONObject jsonObject = resultArray
							.optJSONObject(i);
					Notice entity = new Notice(jsonObject);
					if(1 == entity.getIsRead()){
						return true;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

}
