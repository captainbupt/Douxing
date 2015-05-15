package com.badou.mworking.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.badou.mworking.database.MTrainingDBHelper;

public class TaskDetail {
	private String subject;
	// <subject>测试截止时间</subject>
	// <department>1020000</department>
	private String department;
	// <type>1</type>
	private int type;
	// <longitude>116.441271</longitude>
	private double longitude;
	// <latitude>40.053537</latitude>
	private double latitude;
	// <place>北京市北京市朝阳区清苑路</place>
	private String place;
	// <comment> </comment>
	private String comment;
	// <deadline>1401926400</deadline>
	private long deadline;
	private long startline; //开始时间
	//<photo>0</photo>
	private int photo;//是否有图片上传
	
	public int getPhoto() {
		return photo;
	}

	public void setPhoto(int photo) {
		this.photo = photo;
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	/**
	 * @return the startline
	 */
	public long getStartline() {
		return startline;
	}

	/**
	 * @param  要设置的 startline
	 */
	public void setStartline(long startline) {
		this.startline = startline;
	}

	public TaskDetail(String subject, String department, int type,
			double longitude, double latitude, String place, String comment,long startline,
			long deadline) {
		super();
		this.subject = subject;
		this.department = department;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.place = place;
		this.comment = comment;
		this.startline = startline;
		this.deadline = deadline;
	}

	public TaskDetail() {
		super();
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(MTrainingDBHelper.TASK_SUBJECT, subject);
		values.put(MTrainingDBHelper.TASK_DETAIL_DEPARTMENT, department);
		values.put(MTrainingDBHelper.TASK_DETAIL_TYPE, type);
		values.put(MTrainingDBHelper.TASK_DETAIL_LONGITUDE, longitude);
		values.put(MTrainingDBHelper.TASK_DETAIL_LATITUDE, latitude);
		values.put(MTrainingDBHelper.TASK_DETAIL_PLACE, place);
		values.put(MTrainingDBHelper.TASK_DETAIL_COMMENT, comment);
		values.put(MTrainingDBHelper.TASK_DETAIL_DEADLINE, deadline);
		values.put(MTrainingDBHelper.TASK_DETAIL_STARTLINE,startline);
		return values;
	}
	
	public TaskDetail(Cursor c) {
		super();
		this.subject = c.getString(c.getColumnIndex(MTrainingDBHelper.TASK_SUBJECT));
		this.department = c.getString(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_DEPARTMENT));
		this.type = c.getInt(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_TYPE));
		this.longitude = c.getDouble(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_LONGITUDE));
		this.latitude = c.getDouble(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_LATITUDE));
		this.place = c.getString(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_PLACE));
		this.comment = c.getString(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_COMMENT));
		this.deadline = c.getLong(c.getColumnIndex(MTrainingDBHelper.TASK_DETAIL_DEADLINE));
	}

}
