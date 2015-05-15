package com.badou.mworking.model;

import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

public class Comment {
	
	public Comment(JSONObject jsonObject) {
		super();
		this.name = jsonObject.optString(ResponseParams.COMMENT_USERNAME);
		this.time = jsonObject.optLong(ResponseParams.COMMENT_TIME)*1000;
		this.content = jsonObject.optString(ResponseParams.COMMENT_CONTENT);
	}

	public Comment(String name, long time, String content) {
		super();
		this.name = name;
		this.time = time;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String name;
	private long time;
	private String content;
}
