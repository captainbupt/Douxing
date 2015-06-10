package com.badou.mworking.model.user;

import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

import java.io.Serializable;

public class UserDetail implements Serializable{

	public String name;//名字
	public String dpt;//部门
	public String headimg;//头像地址
	public int score;//分数
	public int ask;// 我的圈帖子数量
	public int share;//分享
	public int nmsg; //聊天未读数
	public int circle_lv; //等级
	public int training_total;//培训总
	public int training_week;//培训周
	public int study_total;//学习总
	public int study_week;//学习周
	public int study_rank;//学习排名
	public int score_rank; //考试排名
	public int study_over; //学习战胜多少人
	public int score_over; //考试战胜了多少人
	
	public UserDetail(JSONObject jo) {
		this.name = jo.optString(RequestParameters.USER_NAME);
		this.dpt = jo.optString(RequestParameters.USER_DPT);
		this.headimg = jo.optString(RequestParameters.USER_HEADIMG);
		this.score = jo.optInt(RequestParameters.USER_SCORE);
		this.ask = jo.optInt(RequestParameters.USER_ASK);
		this.share = jo.optInt(RequestParameters.USER_SHARE);
		this.nmsg = jo.optInt("nmsg");
		this.training_total = jo.optInt(RequestParameters.USER_TRAINING_TOTAL);
		this.training_week = jo.optInt(RequestParameters.USER_TRAINING_WEEK);
		this.study_total = jo.optInt(RequestParameters.USER_STUDY_TOTAL);
		this.study_week = jo.optInt(RequestParameters.USER_STUDY_WEEK);
		this.study_rank = jo.optInt(RequestParameters.USER_STUDY_RANK);
		this.score_rank = jo.optInt(RequestParameters.USER_SCORE_RANK);
		this.study_over = jo.optInt(RequestParameters.USER_STUDY_OVER);
		this.score_over = jo.optInt(RequestParameters.USER_SCORE_OVER);
		this.circle_lv = jo.optInt(ResponseParams.QUESTION_CIRCLE_LV);
	}

	public UserDetail getUserDetail(JSONObject jo) {
		this.name = jo.optString(RequestParameters.USER_NAME);
		this.dpt = jo.optString(RequestParameters.USER_DPT);
		this.headimg = jo.optString(RequestParameters.USER_HEADIMG);
		this.score = jo.optInt(RequestParameters.USER_SCORE);
		this.ask = jo.optInt(RequestParameters.USER_ASK);
		this.share = jo.optInt(RequestParameters.USER_SHARE);
		this.nmsg = jo.optInt("nmsg");
		this.training_total = jo.optInt(RequestParameters.USER_TRAINING_TOTAL);
		this.training_week = jo.optInt(RequestParameters.USER_TRAINING_WEEK);
		this.study_total = jo.optInt(RequestParameters.USER_STUDY_TOTAL);
		this.study_week = jo.optInt(RequestParameters.USER_STUDY_WEEK);
		this.study_rank = jo.optInt(RequestParameters.USER_STUDY_RANK);
		this.score_rank = jo.optInt(RequestParameters.USER_SCORE_RANK);
		this.study_over = jo.optInt(RequestParameters.USER_STUDY_OVER);
		this.score_over = jo.optInt(RequestParameters.USER_SCORE_OVER);
		this.circle_lv = jo.optInt(ResponseParams.QUESTION_CIRCLE_LV);
		return this;
	}
	
}
