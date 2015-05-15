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
 * 类:  <code> Train </code>
 * 功能描述: 培训实体类
 * 创建人:  葛建锋
 * 创建日期: 2014年7月17日 下午5:04:22
 * 开发环境: JDK7.0
 */
public class Train implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String CATEGORY_TRAIN = "training";      //微培训
	public static final String UNREAD_NUM_TRAIN = "trainUnreadNum";//培训的未读数量
	
	private String subject;    //标题 
	private String department;   //部⻔门信息
	private String url;//附件地址
	private String rid;  // 资源唯一标识
	private String imgUrl;//图片下载地址
	private String coursewareScore = ""; //课件打分
	
	private int top = 0;
	private int subtype;         
	private int tag;
	private int feedbackCount = 0;//点赞数量
	private int commentNum = 0;//评论数量
	private int ecnt; //评分人数
	private int eval; //评分总分
	
	
	private long time;         // 时间戳
	
	private int isRead ;        //是否已读（通知公告，为培训标示是否已读）
	private int hasFeedback = Constant.LIKED_NO;    // 是否点赞， 默认没有点赞
	
	/**
	 * 功能描述:  train json 串解析， 得到train实体类对象
	 * @param jsonObject
	 */
	public Train(JSONObject jsonObject) {
		super();
		this.feedbackCount = 0;
		this.commentNum = 0;
		this.rid = jsonObject.optString(ResponseParams.RESOURCE_ID);
		this.subject = jsonObject.optString(ResponseParams.TRAIN_SUBJECT);
		this.department = jsonObject.optString(ResponseParams.TRAIN_DEPARTMENT);
		this.time = jsonObject.optLong(ResponseParams.RESOURCE_TIME) * 1000;
		this.url = jsonObject.optString(ResponseParams.TRAIN_URL);
		this.subtype = jsonObject.optInt(ResponseParams.TRAIN_TYPE);
		this.isRead = jsonObject.optInt(ResponseParams.TRAIN_READ);
		this.tag = Integer.parseInt(jsonObject
				.optString(ResponseParams.TRAIN_TAG));
		this.top = jsonObject.optInt(MTrainingDBHelper.TOP);
		this.imgUrl = jsonObject.optString(ResponseParams.KNOWLEDGE_LIBRARY_IMG);
		String string = jsonObject.optString(ResponseParams.CONTENT);
		try {
			JSONObject jObject = new JSONObject(string);
			int m = jObject.optInt(ResponseParams.M);
			this.coursewareScore = jObject.optString(ResponseParams.E);
			this.hasFeedback = m;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
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

	public String getDepartment() {
		return department;
	}

	public long getTime() {
		return time;
	}

	public String getUrl() {
		return url;
	}

	public String getRid() {
		return rid;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getTag() {
		return tag;
	}

	
	public void setFeedbackCount(int feedbackCount) {
		this.feedbackCount = feedbackCount;
	}

	public int getFeedbackCount() {
		return feedbackCount;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getHasFeedback() {
		return hasFeedback;
	}

	public void setHasFeedback(int hasFeedback) {
		this.hasFeedback = hasFeedback;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
	
	public String getCoursewareScore() {
		return coursewareScore;
	}

	public void setCoursewareScore(String coursewareScore) {
		this.coursewareScore = coursewareScore;
	}
	

	/**
	 * @return the ecnt
	 */
	public int getEcnt() {
		return ecnt;
	}

	/**
	 * @param  要设置的 ecnt
	 */
	public void setEcnt(int ecnt) {
		this.ecnt = ecnt;
	}

	/**
	 * @return the eval
	 */
	public int getEval() {
		return eval;
	}

	/**
	 * @param  要设置的 eval
	 */
	public void setEval(int eval) {
		this.eval = eval;
	}

	public static void putSPJsonArray(Context context, String tag,String userNum,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context,SP.TRAINING, userNum+tag, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.TRAINING, userNum+tag, SPJsonArray2.toString());
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
			String sp = SP.getStringSP(context, SP.DEFAULTCACHE,userNum+Train.UNREAD_NUM_TRAIN, "");
			if(TextUtils.isEmpty(sp)){
				return false;
			}
			try {
				JSONArray resultArray = new JSONArray(sp);
				for (int i = 0 ; i < resultArray.length(); i++) {
					JSONObject jsonObject = resultArray
							.optJSONObject(i);
					Train entity = new Train(jsonObject);
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
