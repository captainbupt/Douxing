package com.badou.mworking.model;

/**
 * 类:  <code> MainPic </code>
 * 功能描述:主页的icon 
 * 创建人: dongqi
 * 创建日期: 2014年8月8日 上午11:51:24
 * 开发环境: JDK6.0
 */
public class MainIcon {
	
	private String mainIconId="";//模块
	private int resId = -1;//图片的url
	private String priority="0"; // 模块的优先级，级别越高，在主页面中显示的越前面，该字段在登录时返回，默认最后一个是更多，倒数第二个是个人中心，所以没有这两个的级别返回
	private String name="";//item显示的名称
	
	public MainIcon(String mainIconId,int url,String name,String priority) {
		this.mainIconId = mainIconId;
		this.name = name;
		this.resId = url;
		this.priority = priority;
	}
	
	
	public String getMainIconId() {
		return mainIconId;
	}

	public void setMainIconId(String mainIconId) {
		this.mainIconId = mainIconId;
	}
	
	public int getResId() {
		return resId;
	}
	public void setResId(int resId) {
		this.resId = resId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
}
