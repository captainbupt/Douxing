package com.badou.mworking.model;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类:  <code> Question </code>
 * 功能描述:  同事圈实体类
 * 创建人:  葛建锋
 * 创建日期: 2014年10月20日 下午3:43:44
 * 开发环境: JDK7.0
 */
public class Question implements Serializable {
	
	public static final String QUESTIONCACHE = "questioncache";

	private static final long serialVersionUID = 1L;
	public  static final int DELETE_OK = 1;    //能够被删除
	public  static final int DELETE_NO = 0;    //不可以删
	
	private int mode = 0;
	private int reply_no;//评论数
	private int credit_no = 0;//点赞数
	private int circle_lv = 0; //等级 
	private int delop = 0;    // 同事圈中的该条信息是否可被删除
	
	public static final int MODE_SHARE = 1;
	public static final int MODE_COMMENT = 2;
 
	private String uid = "";  //用户id
	private String content="";//发布内容
	private String id="";//id?
	private String status="";//状态?
	private String department="";//部门
	private String company="";//企业
	private String qid="";//qid
	private String type="";//类型
	private String employee_id="";//员工号 (登录号? 用户名)
	private String imgUrl="";//头像地址
	private String contentPicUrl="";//内容中图片地址
	private String videourl="";    //视屏下载地址
	private String whom = "";	//私信人的电话号码
	
	private long update_ts;//更新时间
	private long publish_ts;//发布时间
	
	public Question(int mode, String uid,String content, long publish_ts,
			String employee_id, String imgUrl) {
		super();
		this.mode = mode;
		this.content = content;
		this.publish_ts = publish_ts;
		this.employee_id = employee_id;
		this.imgUrl = imgUrl;
		this.uid = uid;
	}
	
	public Question(JSONObject jsonObject, int mode) {
		if (mode == MODE_COMMENT) {
			employee_id = jsonObject.optString(ResponseParams.COMMENT_USERNAME);
			publish_ts = jsonObject.optLong(ResponseParams.COMMENT_TIME) * 1000;
			content = jsonObject.optString(ResponseParams.COMMENT_CONTENT);
			imgUrl = jsonObject.optString(ResponseParams.QUESTION_IMG_URL);
			whom = jsonObject.optString("whom");
		} else {
			uid = jsonObject.optString("uid");
			content = jsonObject.optString(ResponseParams.QUESTION_CONTENT);
			id = jsonObject.optString(ResponseParams.QUESTION_ID);
			status = jsonObject.optString(ResponseParams.QUESTION_STATUS);
			department = jsonObject
					.optString(ResponseParams.QUESTION_DEPARTMENT);
			company = jsonObject.optString(ResponseParams.QUESTION_COMPANY);
			reply_no = Integer.parseInt(jsonObject
					.optString(ResponseParams.QUESTION_REPLY_NO));
			update_ts = Long.parseLong(jsonObject
					.optString(ResponseParams.QUESTION_UPDATE_TS)) * 1000l;
			qid = jsonObject.optString(ResponseParams.QUESTION_QID);
			publish_ts = Long.parseLong(jsonObject
					.optString(ResponseParams.QUESTION_PUBLISH_TS)) * 1000l;
			type = jsonObject.optString(ResponseParams.QUESTION_TYPE);
			employee_id = jsonObject
					.optString(ResponseParams.QUESTION_EMPLOYEE_ID);
			imgUrl = jsonObject.optString(ResponseParams.QUESTION_IMG_URL);
			contentPicUrl = jsonObject.optString(ResponseParams.QUESTION_PIC_URL);
			credit_no = jsonObject.optInt(ResponseParams.QUESTION_CREDIT_NUM);
			circle_lv = jsonObject.optInt(ResponseParams.QUESTION_CIRCLE_LV);
			delop = jsonObject.optInt("delop");
			videourl = jsonObject.optString("videourl");
			whom = jsonObject.optString("whom");
		}
		this.mode = mode;
	}
	
	public ContentValues getValues(){
		ContentValues v = new ContentValues();
		v.put(MTrainingDBHelper.QUAN_QID, qid);
		v.put(MTrainingDBHelper.QUAN_IS_CHECK, 1);
		return v;
	}
	
	public static void putSPJsonArray(Context context,String key,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context, SP.TONGSHIQUAN,key, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.TONGSHIQUAN, key, SPJsonArray2.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getCredit_no() {
		return credit_no;
	}

	public void setCredit_no(int credit_no) {
		this.credit_no = credit_no;
	}
	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getDepartment() {
		return department;
	}

	public String getCompany() {
		return company;
	}

	public int getReply_no() {
		return reply_no;
	}
	
	public void setReply_no(int reply_no) {
		this.reply_no = reply_no;
	}

	public long getUpdate_ts() {
		return update_ts;
	}

	public String getQid() {
		return qid;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public long getPublish_ts() {
		return publish_ts;
	}

	public String getType() {
		return type;
	}

	public String getEmployee_id() {
		return employee_id;
	}

	public String getImgUrl() {
		return imgUrl;
	}
	

	public String getContentPicUrl() {
		return contentPicUrl;
	}

	public void setContentPicUrl(String contentPicUrl) {
		this.contentPicUrl = contentPicUrl;
	}

	public int getCircle_lv() {
		return circle_lv;
	}

	public void setCircle_lv(int circle_lv) {
		this.circle_lv = circle_lv;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public int getDelop() {
		return delop;
	}

	public void setDelop(int delop) {
		this.delop = delop;
	}
	
	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getWhom() {
		return whom;
	}

	public void setWhom(String whom) {
		this.whom = whom;
	}
}
