package com.badou.mworking.util;

/**
 * 类:  <code> MyIntents </code>
 * 功能描述: 
 * 创建人: 葛建锋
 * 创建日期: 2013-12-25 下午1:13:02
 * 开发环境: JDK6.0
 */
public class MyIntents {

	public static final String TYPE = "type";
	public static final String PROCESS_PROGRESS = "process_progress";    
	public static final String URL = "url";
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_INFO = "error_info";
	public static final String IS_PAUSED = "is_paused";
	
	public class Types{

		public static final int PROCESS = 0;
		public static final int COMPLETE = 1;
		
		public static final int START = 2;
		public static final int ADD = 6;     //添加
		public static final int ERROR = 9;
	}
}
