package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.net.RequestParams;
import com.badou.mworking.util.SP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 类:  <code> MainPic </code>
 * 功能描述:主页的icon 
 * 创建人: dongqi
 * 创建日期: 2014年8月8日 上午11:51:24
 * 开发环境: JDK6.0
 */
public class MainIcon {
	
	private String mainIconId="";//模块
	private String url="";//图片的url
	private String priority="0"; // 模块的优先级，级别越高，在主页面中显示的越前面，该字段在登录时返回，默认最后一个是更多，倒数第二个是个人中心，所以没有这两个的级别返回
	private String name="";//item显示的名称
	
	public MainIcon(){
		
	}
	
	public MainIcon(String mainIconId,String url,String name,String priority) {
		this.mainIconId = mainIconId;
		this.name = name;
		this.url = url;
		this.priority = priority;
	}
	
	public List<MainIcon> getMainIcons(Context context){
		ArrayList<MainIcon> mainIcons = new ArrayList<MainIcon>();
		String noticeStr = SP.getStringSP(context,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_NOTICE, "");
		String trainStr = SP.getStringSP(context, SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_TRAIN, "");
		String examStr = SP.getStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_EXAM, "");
		String surverStr = SP.getStringSP(context,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_SURVEY, "");
		String taskStr = SP.getStringSP(context,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_TASK, "");
		String chatterStr = SP.getStringSP(context,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_CHATTER, "");
		String askStr = SP.getStringSP(context,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_ASK, "");
		if(!TextUtils.isEmpty(noticeStr)){
			mainIcons.add(getMainIcon(noticeStr));
		}
		if(!TextUtils.isEmpty(trainStr)){
			mainIcons.add(getMainIcon(trainStr));
		}
		if(!TextUtils.isEmpty(examStr)){
			mainIcons.add(getMainIcon(examStr));
		}
		if(!TextUtils.isEmpty(surverStr)){
			mainIcons.add(getMainIcon(surverStr));
		}
		if(!TextUtils.isEmpty(taskStr)){
			mainIcons.add(getMainIcon(taskStr));
		}
		if(!TextUtils.isEmpty(chatterStr)){
			mainIcons.add(getMainIcon(chatterStr));
		}
		if(!TextUtils.isEmpty(askStr)){
			mainIcons.add(getMainIcon(askStr));
		}
		ComparatorMainIcon comparator = new ComparatorMainIcon();
		Collections.sort(mainIcons, comparator); //对list进行排序
		Collections.reverse(mainIcons);      // 对list集合进行反向
		return mainIcons;
	}
	
	/**
	 * 功能描述: 根据SP中的String，来封装实体类
	 * @return
	 */
	public MainIcon getMainIcon(String mainIconInfoStr){
		String[] mainIconInfo = mainIconInfoStr.split("@");
		return new MainIcon(mainIconInfo[0],mainIconInfo[1],mainIconInfo[2],mainIconInfo[3]);
	}
	
	public String mainIconToString(String mainIconId,String url,String name,String priority){
		return mainIconId+"@"+url+"@"+name+"@"+priority+"@";
	}
	
	/**
	 * 功能描述: 清除SP中保存的首页内容
	 */
	public void clear(Context context){
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_NOTICE, "");
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_TRAIN, "");
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_EXAM, "");
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_SURVEY, "");
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_TASK, "");
		SP.putStringSP(context, SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_CHATTER, "");
		SP.putStringSP(context, SP.DEFAULTCACHE,RequestParams.CHK_UPDATA_PIC_ASK, "");
	}
	
	
	public String getMainIconId() {
		return mainIconId;
	}

	public void setMainIconId(String mainIconId) {
		this.mainIconId = mainIconId;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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


	class ComparatorMainIcon implements Comparator<MainIcon>{

		@Override
		public int compare(MainIcon mainIcon1, MainIcon mainIcon2) {
			return Integer.valueOf(mainIcon1.getPriority())>Integer.valueOf(mainIcon2.getPriority()) ? 1:-1;
		}
	}
	
}
