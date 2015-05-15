package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类: <code> Exam </code> 功能描述: 考试实体类 创建人: 葛建锋 创建日期: 2014年7月17日 下午5:11:49 开发环境:
 * JDK7.0
 */
public class Exam implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String CATEGORY_EXAM = "exam";        //在线考试
	public static final String UNREAD_NUM_EXAM = "examUnreadNum";//考试的未读数量

	public  static final int  DEFUALT_PI_YUE = 0;     //待批阅
	public  static final int  PIYUE_COMPLETE = 1;     // 批阅完成
	 
	private int tag;
	private int subtype;    //后台返回的数据格式
	private int score = 0;     //得分，默认为0
	private int pass = 0;   // 该门课的及格分数，大于等于该分数，就代表及格了
	private int top = 0;
	private int allScore = 0;
	private int isDaipiyue = Exam.DEFUALT_PI_YUE; //包含在content中，是否已批阅       ，done == 1代表已完成， done==0代表待批阅
	private int offline ;//是否过期
	private int read = 0;    //是否已完成          0代表未考完，    1代表已考完 
	
	private long time;
	
	private String examId;      // 主键id ，资源id
	private String subject;
	private String examUrl; //试卷的下载地址
	private String department;
	private String credit; //学分
	
	public Exam(){}
	
	public Exam(JSONObject json) {
		this.examId = json.optString(MTrainingDBHelper.RESOURCE_ID);
		this.subject = json.optString(MTrainingDBHelper.EXAM_SUBJECT);
		if(json.has(MTrainingDBHelper.EXAM_TYPE)){
			this.subtype = json.optInt(MTrainingDBHelper.EXAM_TYPE);
		}else{
			this.subtype = Constant.MWKG_FORAMT_TYPE_XML;
		}
		this.top = json.optInt(MTrainingDBHelper.TOP);
		this.department = json.optString(MTrainingDBHelper.EXAM_DEPARTMENT);
		this.time = json.optLong(MTrainingDBHelper.RESOURCE_TIME)*1000;
		this.examUrl = json.optString(MTrainingDBHelper.URL);
		this.tag = json.optInt(MTrainingDBHelper.CATE_TAG);
		this.offline = json.optInt(MTrainingDBHelper.OFFLINE);
		if(json.has("read")){
			this.read = json.optInt("read");
		}else{
			this.read = Exam.PIYUE_COMPLETE;
		}
		this.credit = json.optString("credit");
		this.pass = json.optInt("pass");
		String strContent = json.optString(ResponseParams.EXAM_CONTENT);
		JSONObject jsonContent =null;
		if (strContent != null && !strContent.equals("")) {
			try {
				jsonContent = new JSONObject(strContent);
				if (jsonContent != null) {
					this.score = jsonContent.optInt(ResponseParams.EXAM_S);
					this.allScore = jsonContent.optInt(ResponseParams.EXAM_T);
					this.isDaipiyue = jsonContent.optInt(ResponseParams.EXAM_D);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getExamUrl() {
		return examUrl;
	}

	public void setExamUrl(String examUrl) {
		this.examUrl = examUrl;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getOffline() {
		return offline;
	}

	public void setOffline(int offline) {
		this.offline = offline;
	}

	public int getAllScore() {
		return allScore;
	}

	public void setAllScore(int allScore) {
		this.allScore = allScore;
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

	public int getType() {
		return subtype;
	}

	public void setType(int type) {
		this.subtype = type;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		this.examId = examId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
	
	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}
	
	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	public boolean isFinish(){
		if(this.read == 1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isDaiPiYue(){
		if(this.isDaipiyue == DEFUALT_PI_YUE){
			return true;
		}else{
			return false;
		}
	}
	
	public static void putSPJsonArray(Context context, String tag, String userNum,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context, SP.EXAM,userNum+tag, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.EXAM, userNum+tag, SPJsonArray2.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean getUnreadNum(Context context){
		String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(context,SP.DEFAULTCACHE, userNum+Exam.UNREAD_NUM_EXAM, "");
		if(TextUtils.isEmpty(sp)){
			return false;
		}
		try {
			JSONArray resultArray = new JSONArray(sp);
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Exam exam = new Exam(jsonObject);
				if(!exam.isFinish()){
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
