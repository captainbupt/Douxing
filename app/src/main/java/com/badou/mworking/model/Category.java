package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.util.SP;

import java.util.ArrayList;

/**
 * 类:  <code> Category </code>
 * 功能描述:  通知公告，在线考试，微培训，任务签到分类信息
 * 创建人:  葛建锋
 * 创建日期: 2014年10月14日 上午10:15:08
 * 开发环境: JDK7.0
 */
public class Category {
	
	public static String CLICKMAINICON = "";
	public static final String CATEGORY_NOTICE = "notice";     //通知公告
	public static final String CATEGORY_EXAM = "exam";        //在线考试
	public static final String CATEGORY_TASK = "task";        //任务签到
	public static final String CATEGORY_TRAIN = "training";   //微培训
	public static final String CATEGORY_RANK = "rank";      //等级考试

	private int tag;
	private String name;
	
	/**
	 * 功能描述:
	 */
	public Category() {
		super();
	}

	public Category(String name,int tag) {
		super();
		this.tag = tag;
		this.name = name;
	}
	
	/**
	 * 功能描述: 封装 sp中存放内容的String字符串
	 */
	public String categoryToString(String name,int tag){
		return name+"@"+tag+",";
	}
	
	public static ArrayList<Category> getCategoryList(String categoryInfoStr){
		if(categoryInfoStr!=null&&!categoryInfoStr.equals("")){
			ArrayList<Category> categorys = new ArrayList<Category>();
			String[] category = categoryInfoStr.split(",");
			try {
				for (String string : category) {
					String[] categoryInfo = string.split("@");
					categorys.add(new Category(categoryInfo[0],Integer.valueOf(categoryInfo[1])));
				}
				return categorys;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				return new ArrayList<Category>();
			}
		}
		return new ArrayList<Category>();
	}
	
	public int getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}
	
	public static String getTitleName(Context mContext,String key,int tag){
		String title = "";
		ArrayList<Category> list = getCategoryList(SP.getStringSP(mContext,SP.DEFAULTCACHE, key, "0"));
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTag() == tag) {
				title = list.get(i).getName();
			}
		}
		return title;
	}
	
	public static int getTitleTag(Context mContext,String key,String name){
		if(TextUtils.isEmpty(name)){
			return 0;
		}
		int tag = 0;
		ArrayList<Category> list = getCategoryList(SP.getStringSP(mContext,SP.DEFAULTCACHE, key, "0"));
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(name)) {
				tag = list.get(i).getTag();
			}
		}
		return tag;
	}
}
