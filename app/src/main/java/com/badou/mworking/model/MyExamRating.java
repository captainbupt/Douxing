package com.badou.mworking.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author gejianfeng
 * 我的考试等级
 */
public class MyExamRating implements Serializable{

	private String self_score;   //自己得分
	private  int avg_score;    //平均分
	private String title_now;    //当前等级
	private String title_next;   //下一等级
	
	public MyExamRating(JSONObject jsonObject) {
		super();
		this.self_score = jsonObject.optString("self_score");
		this.avg_score = jsonObject.optInt("avg_score");
		this.title_now = jsonObject.optString("title_now");
		this.title_next = jsonObject.optString("title_next");
	}
	
	public String getSelf_score() {
		return self_score;
	}
	public void setSelf_score(String self_score) {
		this.self_score = self_score;
	}
	public int getAvg_score() {
		return avg_score;
	}
	public void setAvg_score(int avg_score) {
		this.avg_score = avg_score;
	}
	public String getTitle_now() {
		return title_now;
	}
	public void setTitle_now(String title_now) {
		this.title_now = title_now;
	}
	public String getTitle_next() {
		return title_next;
	}
	public void setTitle_next(String title_next) {
		this.title_next = title_next;
	}
	
}
