package com.badou.mworking.model;

/**
 * 功能描述:主页的icon
 */
public class MainIcon {
	
	public String mainIconId="";//模块
	public int resId = -1;//图片的url
	public String priority="0"; // 模块的优先级，级别越高，在主页面中显示的越前面，该字段在登录时返回，默认最后一个是更多，倒数第二个是个人中心，所以没有这两个的级别返回
	public String name="";//item显示的名称
	
	public MainIcon(String mainIconId,int url,String name,String priority) {
		this.mainIconId = mainIconId;
		this.name = name;
		this.resId = url;
		this.priority = priority;
	}
}
