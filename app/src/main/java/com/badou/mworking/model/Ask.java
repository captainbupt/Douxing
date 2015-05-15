package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author 葛建锋
 * 问答实体类
 */
public class Ask implements Serializable {
	
	public final static String WENDACACHE = "wendaCache";

    private String uid;
    private String content; //内容
    private String id;
    private String circle_rp;
    private int count;  //回答人数
    private String status;
    private String picurl; //问题 图片地址
    private String aid;
    private String type;
    private Long create_ts;     //创建时间
    private String eid;       // 姓名
    private String imgurl;  //头像地址
    private String whom;    //私信聊天whom
    private int circle_lv; //等级
    private int delop;  // 判断是否可以删除，即判断是不是管理员，管理员具有删除权限
    
	/**
	 * 功能描述: 通知公告json解析
	 * @param jsonObject
	 */
	public Ask(JSONObject jsonObject) {
		super();
		this.uid = jsonObject.optString("uid");
		this.content = jsonObject.optString("content");
		this.id = jsonObject
				.optString("id");
		this.circle_rp = jsonObject.optString("circle_rp") ;
		this.count = jsonObject.optInt("count");
		this.status = jsonObject.optString("status");
		this.picurl = jsonObject.optString("picurl");
		this.aid = jsonObject.optString("aid");
		this.type = jsonObject
				.optString("type");
		this.create_ts = jsonObject
				.optLong("create_ts");
		this.eid = jsonObject
				.optString("eid");
		this.circle_lv = jsonObject
				.optInt("circle_lv");
		this.imgurl = jsonObject
				.optString("imgurl");
		this.delop = jsonObject.optInt("delop");
		this.whom = jsonObject.optString("whom");
	}
	
	public static void putSPJsonArray(Context context,String key,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context, SP.WENDA, key, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.WENDA, key, SPJsonArray2.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCircle_rp() {
		return circle_rp;
	}
	public void setCircle_rp(String circle_rp) {
		this.circle_rp = circle_rp;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEid() {
		return eid;
	}
	public Long getCreate_ts() {
		return create_ts;
	}
	public void setCreate_ts(Long create_ts) {
		this.create_ts = create_ts;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public int getCircle_lv() {
		return circle_lv;
	}
	public void setCircle_lv(int circle_lv) {
		this.circle_lv = circle_lv;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public int getDelop() {
		return delop;
	}
	public void setDelop(int delop) {
		this.delop = delop;
	}
	public String getWhom() {
		return whom;
	}
	public void setWhom(String whom) {
		this.whom = whom;
	}
}
