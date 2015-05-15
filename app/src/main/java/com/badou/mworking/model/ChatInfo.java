/* 
 * 文件名: ChatInfo.java
 * 包路径: com.badou.mworking.model
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年9月19日 上午10:23:47
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年9月19日 上午10:23:47 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类:  <code> ChatInfo </code>
 * 功能描述: 聊天页面
 * 创建人:  葛建锋
 * 创建日期: 2014年9月19日 上午10:23:47
 * 开发环境: JDK7.0
 */
public class ChatInfo implements Serializable{

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;
	
	private long ts;  //消息时间
	private String content;   //消息内容
	private String own;      //谁的消息，表示符
	
	public ChatInfo() {
		super();
	}
	
	public ChatInfo(JSONObject jo) {
		super();
		this.ts = jo.optLong("ts");
		this.content = jo.optString("content");
		this.own = jo.optString("own");
	}
	
	/**
	 * @return the ts
	 */
	public long getTs() {
		return ts;
	}
	/**
	 * @param  要设置的 ts
	 */
	public void setTs(long ts) {
		this.ts = ts;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param  要设置的 content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the own
	 */
	public String getOwn() {
		return own;
	}

	/**
	 * @param  要设置的 own
	 */
	public void setOwn(String own) {
		this.own = own;
	}
}
