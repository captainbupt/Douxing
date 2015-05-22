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

/**
 * 功能描述: 考试实体类
 */
public class Exam extends Category {

	public static final int CATEGORY_TYPE = Category.CATEGORY_EXAM;
	public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
	public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//考试 的 未读数量

	public  static final int  DEFUALT_PI_YUE = 0;     //待批阅
	public  static final int  PIYUE_COMPLETE = 1;     // 批阅完成

	public int subtype;    //后台返回的数据格式
	public int score = 0;     //得分，默认为0
	public int pass = 0;   // 该门课的及格分数，大于等于该分数，就代表及格了
	public int allScore = 0;
	public int isDaipiyue = Exam.DEFUALT_PI_YUE; //包含在content中，是否已批阅       ，done == 1代表已完成， done==0代表待批阅
	public int offline ;//是否过期
	public int read = 0;    //是否已完成          0代表未考完，    1代表已考完 
	
	public long time;

	public String url; //试卷的下载地址
	public String department;
	public String credit; //学分

	
	public Exam(JSONObject jsonObject) {
		super(jsonObject);
		if(jsonObject.has(MTrainingDBHelper.EXAM_TYPE)){
			this.subtype = jsonObject.optInt(MTrainingDBHelper.EXAM_TYPE);
		}else{
			this.subtype = Constant.MWKG_FORAMT_TYPE_XML;
		}
		this.offline = jsonObject.optInt(MTrainingDBHelper.OFFLINE);
		if(jsonObject.has("read")){
			this.read = jsonObject.optInt("read");
		}else{
			this.read = Exam.PIYUE_COMPLETE;
		}
		this.credit = jsonObject.optString("credit");
		this.pass = jsonObject.optInt("pass");
		String strContent = jsonObject.optString(ResponseParams.EXAM_CONTENT);
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
	
	public static boolean getUnreadNum(Context context){
		String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(context,SP.DEFAULTCACHE, userNum+Exam.CATEGORY_KEY_UNREAD_NUM, "");
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
