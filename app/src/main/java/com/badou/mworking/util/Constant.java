/* 
 * 文件名: Constant.java
 * 包路径: com.badou.mtraining.util
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年7月18日 下午8:53:45
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年7月18日 下午8:53:45 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.util;

/**
 * 功能描述: 常量类
 */
public class Constant {

	/**
	 * 记录显示的tag
	 */
	public final static int LIST_ITEM_NUM = 10;
	public final static int LIST_AROUND_NUM = 100;

	
	public static final String LANGUAGE = "language";
	public static final String COMPANY = "company";
    public static final String LV_URL = "http://mworking.cn:8421/webview/userTitle.html?uid="; // 访问等级页面的url
    
    public static final int MWKG_FORAMT_TYPE_HTML = 1;      //html格式，用webview
    public static final int MWKG_FORAMT_TYPE_PDF = 2; 	    //pdf格式，安卓调pdf阅读器，ios调webview
    public static final int MWKG_FORAMT_TYPE_XML = 3;	    //xml格式
    public static final int MWKG_FORAMT_TYPE_MPEG = 4;  	//mpeg4格式，调系统播放器
    public static final int MWKG_FORAMT_TYPE_RAW = 5;		//自定义格式，程序解析处理
    public static final int MWKG_FORAMT_TYPE_MP3 = 6;		//MP3格式，程序解析处理

	public static final int READ_YES = 1;
	public static final int READ_NO = 0;
	public static final int TOP_YES=1;
	public static final int LIKED_YES=1;
	public static final int LIKED_NO=0;
	public static final int FINISH_YES =1;
	public static final int OVERDUE_YES =1;
	public static final int UPLOAD_PHOTO_YES=1;
	public static final int FINISH_NO =0;
	
	public static final String TRAIN_IMG_SHOW = "http://mworking.cn/Uploads/training/";
	public static final String TRAIN_IMG_FORMAT = ".png";

}
