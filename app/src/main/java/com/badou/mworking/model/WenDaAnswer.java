package com.badou.mworking.model;

import android.content.ContentValues;

import com.badou.mworking.database.MTrainingDBHelper;

import org.json.JSONObject;

/**
 * @author 葛建锋 问答回答实体类
 */
public class WenDaAnswer {

	private String uid;
	private String content;
	private String id;
	private String circle_rp;
	private int count;
	private String status;
	private String picurl;
	private String aid;
	private String type;
	private Long create_ts;
	private String eid; 
	private String circle_lv;
	private String imgurl;
	
	public WenDaAnswer(JSONObject jsonObject){
		this.uid = jsonObject.optString("uid");
		this.content = jsonObject.optString("content");
		this.id = jsonObject.optString("id");
		this.circle_rp = jsonObject.optString("circle_rp");
		this.count = jsonObject.optInt("count");
		this.status = jsonObject.optString("status");
		this.picurl = jsonObject.optString("picurl");
		this.aid = jsonObject.optString("aid");
		this.type = jsonObject.optString("type");
		this.create_ts = jsonObject.optLong("create_ts");
		this.eid = jsonObject.optString("eid");
		this.circle_lv = jsonObject.optString("circle_lv");
		this.imgurl = jsonObject.optString("imgurl");
	}
	
	public ContentValues getValues(){
		ContentValues contentValues = new ContentValues();
		contentValues.put(MTrainingDBHelper.WENDA_QID, aid+create_ts);
		return contentValues;
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
	public String getStatus() {
		return status;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
	public Long getCreate_ts() {
		return create_ts;
	}
	public void setCreate_ts(Long create_ts) {
		this.create_ts = create_ts;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public String getCircle_lv() {
		return circle_lv;
	}
	public void setCircle_lv(String circle_lv) {
		this.circle_lv = circle_lv;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
}
