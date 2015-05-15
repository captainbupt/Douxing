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
 * 类: <code> Task </code> 功能描述: 任务签到实体类 创建人: 葛建锋 创建日期: 2014年7月17日 下午5:18:57
 * 开发环境: JDK7.0
 */
public class Task implements Serializable {
	// {
	// "ts": "1418004511", //下发时间
	// "startline": 1417765680, //开始时间
	// "department": "八斗\/测试", //部门
	// "tag": "2", //分类
	// "subject": "这是第二个签到任务", //标题
	// "img": "", //
	// "type": "4", //类型
	// "offline": 0, //是否过期                     1代表已过期
	// "photo": 1,//是否上传照片
	// xml 地址
	// "url":
	// "http:\/\/mworking.cn\/badou\/task?rid=11f86cdf138c0efc118395480503ed18",
	// 图片地址
	// "content":"{\"c\":1}" //是否完成
	// "{\"p\":\"http:\\\/\\\/mworking.cn\\\/checkin\\\/badou\\\/11f86cdf138c0efc118395480503ed18\\\/10086.jpeg\"}",
	// "rid": "11f86cdf138c0efc118395480503ed18",
	// "subtype": 3,
	// "read": 1,
	// "longitude": 0,
	// "latitude": 0,
	// "comment": "这是自由签到啊！！！",
	// "place": " ",//地点
	// "deadline": 1418968080,
	// "top": "0" //是否置顶
	// }

	private static final long serialVersionUID = 1L;
	public static final String CATEGORY_TASK = "task";        //任务签到
	public static final String UNREAD_NUM_TASK = "taskUnreadNum";//签到的未读数量
	public static final String TASK_FRAGMENT_ITEM_POSITION = "task_position";
	public static final String SIGN_BACK_TASK_FRAGMENT = "s2task";
	
	private int subtype;// 类型
	private int tag;// catetag
	private int top;// 是否置顶
	private int overdue;// 是否过期
	private int type;// TYPE
	private int photo;// 是否上传照片
	private int read;  //是否已经完成签到         1代表已经签到

	public boolean isFinish = false;
	
	private long startline;// 开始时间
	private long deadline;// 结束时间
	private long publishTime;// 下发时间

	private double longitude;// 经度
	private double latitude;// 纬度
	
	private String rid;// rid
	private String subject;// 标题
	private String department;// 部门
	private String url;// xml的下载地址
	private String photoUrl;// 图片地址
	private String place;// 显示的地址
	private String img;
	private String comment;// 描述

	public Task(JSONObject jsonObject) {
		super();
		this.rid = jsonObject.optString(ResponseParams.RESOURCE_ID);
		this.subject = jsonObject.optString(ResponseParams.TASK_SUBJECT);
		this.department = jsonObject.optString(ResponseParams.TASK_DEPARTMENT);
		this.url = jsonObject.optString(ResponseParams.TASK_URL);
		this.subtype = jsonObject.optInt(ResponseParams.TASK_SUBTYPE);
		this.publishTime = jsonObject.optLong(ResponseParams.RESOURCE_TIME) * 1000;
		this.top = jsonObject.optInt(MTrainingDBHelper.TOP);
		this.tag = jsonObject.optInt(MTrainingDBHelper._TAG);
		this.overdue = jsonObject.optInt(ResponseParams.OFFLINE);
		this.type = jsonObject.optInt(ResponseParams.TASK_DETAIL_TYPE);
		this.comment = jsonObject.optString(ResponseParams.TASK_DETAIL_COMMENT);
		this.startline = jsonObject.optLong(ResponseParams.TASK_DETAIL_STARTLINE) * 1000;
		this.img = jsonObject.optString(ResponseParams.IMG);
		this.photo = jsonObject.optInt(ResponseParams.TASK_PHOTO);
		this.read = jsonObject.optInt("read");
		try {
			this.latitude = jsonObject.getDouble(ResponseParams.TASK_DETAIL_LATITUDE);
			this.longitude = jsonObject.getDouble(ResponseParams.TASK_DETAIL_LONGITUDE);
		} catch (JSONException e1) {
			this.latitude = 0;
			this.longitude = 0;
			e1.printStackTrace();
		}
		String contentStr = jsonObject.optString(ResponseParams.CONTENT);
		this.place = jsonObject.optString(ResponseParams.TASK_DETAIL_PLACE);

		if (null != contentStr && !"".equals(contentStr)) {
			try {
				JSONObject jsonContent = new JSONObject(contentStr);
				this.photoUrl = jsonContent.optString(ResponseParams.P);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public int getPhoto() {
		return photo;
	}

	public void setPhoto(int photo) {
		this.photo = photo;
	}

	public int getOverdue() {
		return overdue;
	}

	public void setOverdue(int overdue) {
		this.overdue = overdue;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getStartline() {
		return startline;
	}

	public void setStartline(long startline) {
		this.startline = startline;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public void setTime(long time) {
		this.publishTime = time;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public String getRid() {
		return rid;
	}

	public String getSubject() {
		return subject;
	}

	public String getDepartment() {
		return department;
	}

	public String getUrl() {
		return url;
	}

	public int getSubtype() {
		return subtype;
	}

	public long getTime() {
		return publishTime;
	}

	public long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public boolean isFinish() {
		if (read == Constant.FINISH_YES) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isOverdue() {
		if (overdue == Constant.OVERDUE_YES) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isUpLoadPhoto(){
		if (photo == Constant.UPLOAD_PHOTO_YES) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void putSPJsonArray(Context context, String tag,String userNum,String SPJSONArray,JSONArray jsonArray){
		try {
			if(TextUtils.isEmpty(SPJSONArray)){
				SP.putStringSP(context,SP.TASK, userNum+tag, jsonArray.toString());
			}else{
				JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
				int length = jsonArray.length();
				for(int i = 0; i<length; i++){
					SPJsonArray2.put(jsonArray.opt(i));
				}
				SP.putStringSP(context,SP.TASK, userNum+tag, SPJsonArray2.toString());
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
			String sp = SP.getStringSP(context,SP.DEFAULTCACHE, userNum+Task.UNREAD_NUM_TASK, "");
			if(TextUtils.isEmpty(sp)){
				return false;
			}
			try {
				JSONArray resultArray = new JSONArray(sp);
				for (int i = 0 ; i < resultArray.length(); i++) {
					JSONObject jsonObject = resultArray
							.optJSONObject(i);
					Task entity = new Task(jsonObject);
					if(!entity.isFinish()){
						return true;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
