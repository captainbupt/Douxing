package com.badou.mworking.entity;

import android.content.Context;

import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gejianfeng
 * 分类实体类
 * 说明： 分类最多支持到3级，目前做到两级就够了
 */
public class Classification {
	
	public boolean hasErjiClassification = false;   // 是否含有二级分类

	private String name;  //分类名称
	private int tag ;   //分类tag
	private int priority;  //分类优先级
	private List<Object> classifications = null;
	
	public Classification(Context context,JSONObject json,String ClassificationCashName) {
		classifications = new ArrayList<>();
		this.name = json.optString("name");
		this.tag = json.optInt("tag");
		SP.putStringSP(context, ClassificationCashName, this.tag+"", this.name);     // 保存分类，目的1： 在全部列表里面，点击item进入，title需要显示分类名，这样方便， 2 在个人中心我的学习页面，还得靠这种方式，来获取，要不
		this.priority = json.optInt("priority");                                              // 点击进入没有title   3. 如果用缓存的json串获取的话，还得解析便利，降低效率
		JSONArray sonArray = json.optJSONArray("son");
		if(sonArray!=null&&sonArray.length()>0){
			hasErjiClassification = true;
			int nextClassificationLenght = sonArray.length();
			for(int i = 0 ; i < nextClassificationLenght ; i++ ){
				JSONObject twoJsonObj;
				try {
					twoJsonObj = sonArray.getJSONObject(i);
					Classification classification = new Classification(context,twoJsonObj,ClassificationCashName);
					classifications.add(classification);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public List<Object> getClassifications() {
		return classifications;
	}
	public void setClassifications(List<Object> classifications) {
		this.classifications = classifications;
	}
}
