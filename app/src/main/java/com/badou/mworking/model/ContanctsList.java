/* 
 * 文件名: ContanctsList.java
 * 包路径: com.badou.mworking.fragment
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年9月19日 上午9:53:16
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年9月19日 上午9:53:16 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类:  <code> ContanctsList </code>
 * 功能描述: 聊天列表实体类
 * 创建人:  葛建锋
 * 创建日期: 2014年9月19日 上午9:53:16
 * 开发环境: JDK7.0
 */
public class ContanctsList implements Serializable{

	private static final long serialVersionUID = 1L;
	private String img;      //联系人头像url
	private String content;   //联系人列表显示说过的话
	private long ts;          //时间
	private String own;      //我的电话号码
	private String whom;     // 聊天人的电话号码
	private int msgcnt;    //信息条数
	private String name;      //聊天人的姓名
	
	public ContanctsList(String img, String content, long ts, String own,
			String whom, int msgcnt, String name) {
		super();
		this.img = img;
		this.content = content;
		this.ts = ts;
		this.own = own;
		this.whom = whom;
		this.msgcnt = msgcnt;
		this.name = name;
	}
	
	public ContanctsList(JSONObject dataJson) {
		this.whom = dataJson.optString("whom");
		this.name = dataJson.optString("name");
		this.img = dataJson.optString("img");
		this.msgcnt = dataJson.optInt("msgcnt");
		JSONObject msginfo = dataJson.optJSONObject("msginfo");
		if(msginfo!= null){
			this.content = msginfo.optString("content");
			this.ts = msginfo.optLong("ts");
			this.own = msginfo.optString("own");
		}
	}
	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}
	/**
	 * @param  要设置的 img
	 */
	public void setImg(String img) {
		this.img = img;
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
	/**
	 * @return the whom
	 */
	public String getWhom() {
		return whom;
	}
	/**
	 * @param  要设置的 whom
	 */
	public void setWhom(String whom) {
		this.whom = whom;
	}
	/**
	 * @return the 未读数
	 */
	public int getMsgcnt() {
		return msgcnt;
	}
	/**
	 * @param  要设置的 msgcnt
	 */
	public void setMsgcnt(int msgcnt) {
		this.msgcnt = msgcnt;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param  要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
