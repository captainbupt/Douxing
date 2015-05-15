package com.badou.mworking.model.user;

import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

import java.io.Serializable;

public class UserDetail implements Serializable{

	private String name;//名字
	private String dpt;//部门
	private String headimg;//头像地址
	private int score;//分数
	private int ask;// 我的圈帖子数量
	private int share;//分享
	private int nmsg; //聊天未读数
	private int circle_lv; //等级
	private int training_total;//培训总
	private int training_week;//培训周
	private int study_total;//学习总
	private int study_week;//学习周
	private int study_rank;//学习排名
	private int score_rank; //考试排名
	private int study_over; //学习战胜多少人
	private int score_over; //考试战胜了多少人
	
	public UserDetail(JSONObject jo) {
		this.name = jo.optString(RequestParams.USER_NAME);
		this.dpt = jo.optString(RequestParams.USER_DPT);
		this.headimg = jo.optString(RequestParams.USER_HEADIMG);
		this.score = jo.optInt(RequestParams.USER_SCORE);
		this.ask = jo.optInt(RequestParams.USER_ASK);
		this.share = jo.optInt(RequestParams.USER_SHARE);
		this.nmsg = jo.optInt("nmsg");
		this.training_total = jo.optInt(RequestParams.USER_TRAINING_TOTAL);
		this.training_week = jo.optInt(RequestParams.USER_TRAINING_WEEK);
		this.study_total = jo.optInt(RequestParams.USER_STUDY_TOTAL);
		this.study_week = jo.optInt(RequestParams.USER_STUDY_WEEK);
		this.study_rank = jo.optInt(RequestParams.USER_STUDY_RANK);
		this.score_rank = jo.optInt(RequestParams.USER_SCORE_RANK);
		this.study_over = jo.optInt(RequestParams.USER_STUDY_OVER);
		this.score_over = jo.optInt(RequestParams.USER_SCORE_OVER);
		this.circle_lv = jo.optInt(ResponseParams.QUESTION_CIRCLE_LV);
	}

	public UserDetail getUserDetail(JSONObject jo) {
		this.name = jo.optString(RequestParams.USER_NAME);
		this.dpt = jo.optString(RequestParams.USER_DPT);
		this.headimg = jo.optString(RequestParams.USER_HEADIMG);
		this.score = jo.optInt(RequestParams.USER_SCORE);
		this.ask = jo.optInt(RequestParams.USER_ASK);
		this.share = jo.optInt(RequestParams.USER_SHARE);
		this.nmsg = jo.optInt("nmsg");
		this.training_total = jo.optInt(RequestParams.USER_TRAINING_TOTAL);
		this.training_week = jo.optInt(RequestParams.USER_TRAINING_WEEK);
		this.study_total = jo.optInt(RequestParams.USER_STUDY_TOTAL);
		this.study_week = jo.optInt(RequestParams.USER_STUDY_WEEK);
		this.study_rank = jo.optInt(RequestParams.USER_STUDY_RANK);
		this.score_rank = jo.optInt(RequestParams.USER_SCORE_RANK);
		this.study_over = jo.optInt(RequestParams.USER_STUDY_OVER);
		this.score_over = jo.optInt(RequestParams.USER_SCORE_OVER);
		this.circle_lv = jo.optInt(ResponseParams.QUESTION_CIRCLE_LV);
		return this;
	}

	public int getStudy_rank() {
		return study_rank;
	}

	public void setStudy_rank(int study_rank) {
		this.study_rank = study_rank;
	}

	public int getScore_rank() {
		return score_rank;
	}

	public void setScore_rank(int score_rank) {
		this.score_rank = score_rank;
	}

	public UserDetail() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDpt() {
		return dpt;
	}

	public void setDpt(String dpt) {
		this.dpt = dpt;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getAsk() {
		return ask;
	}

	public void setAsk(int ask) {
		this.ask = ask;
	}

	public int getShare() {
		return share;
	}

	public void setShare(int share) {
		this.share = share;
	}

	public int getTraining_total() {
		return training_total;
	}

	public void setTraining_total(int training_total) {
		this.training_total = training_total;
	}

	public int getTraining_week() {
		return training_week;
	}

	public void setTraining_week(int training_week) {
		this.training_week = training_week;
	}

	public int getStudy_total() {
		return study_total;
	}

	public void setStudy_total(int study_total) {
		this.study_total = study_total;
	}

	public int getStudy_week() {
		return study_week;
	}

	public void setStudy_week(int study_week) {
		this.study_week = study_week;
	}

	public int getStudy_over() {
		return study_over;
	}

	public void setStudy_over(int study_over) {
		this.study_over = study_over;
	}

	public int getScore_over() {
		return score_over;
	}

	public void setScore_over(int score_over) {
		this.score_over = score_over;
	}

	public int getCircle_lv() {
		return circle_lv;
	}

	public void setCircle_lv(int circle_lv) {
		this.circle_lv = circle_lv;
	}

	public int getNmsg() {
		return nmsg;
	}

	public void setNmsg(int nmsg) {
		this.nmsg = nmsg;
	}
}
