package com.badou.mworking.net;

/**
 * Created by yee on 3/7/14.
 */
public class RequestParams {
	/**
	 * overall
	 */
	public static final String SERIAL="serial";//手机号
	public static final String LOCATION = "gps";//gps
	public static final String LOCATION_LATITUDE = "lat";//精度
	public static final String LOCATION_LONGITUDE = "lon";//维度
	public static final String USER_ID = "uid";//用户id
	public static final String RESOURCE_ID = "rid";//资源id
	/**
	 * login
	 */
	public static final String l_USERNAME = SERIAL;//手机号
	public static final String l_PASSWORD = "pwd";//密码
	/**
	 * change password
	 */
	public static final String cp_ORIGINAL_PASSWORD = "oldpwd";//旧密码
	public static final String cp_NEW_PASSWORD = "newpwd";//新密码
	public static final String cp_USER_PHONE = SERIAL;//手机号
	public static final String cp_VCODE = "vcode";//验证码
	public static final String cp_GPS_LON = "gps";//gps
	/**
	 * submit exam
	 */
	public static final String se_ANSWERS_ALL = "items";//提交试卷
	public static final String se_ANSWERS = "a";//每道题答案
	/**
	 * feedback
	 */
	public static final String fb_FEEDBACK = "any";//意见反馈
	/**
	 * comments
	 */
	public static final String cm_PAGENUMBER = "page_no";//加载的页数
	public static final String cm_ITEMPERPAGE = "item_per_page";//每页加载的数量
	public static final String ERRCODE = "errcode";//错误码
	/**
	 * userdetail
	 * */
	public static final String USER_STUDY_WEEK = "study_week";//周学习进度
	public static final String USER_STUDY_TOTAL = "study_total";//总学习进度
	public static final String USER_TRAINING_WEEK = "training_week";//周考试进度
	public static final String USER_TRAINING_TOTAL = "training_total";//总考试进度
	public static final String USER_SHARE = "share";//分享
	public static final String USER_ASK = "ask";//问答
	public static final String USER_SCORE = "score";//分数
	public static final String USER_HEADIMG = "headimg";//用户头像
	public static final String USER_DPT = "dpt";//部门
	public static final String USER_NAME = "name";//用户名称
	public static final String USER_DATA = "data";//用户数据
	public static final String USER_ERRCODE = "errcode";//错误码
	public static final String NOTICE_READ = "read";//已读
	public static final String USER_STUDY_RANK = "study_rank";//学习排名
	public static final String USER_SCORE_RANK = "score_rank";//考试排名
	public static final String USER_STUDY_OVER = "study_over";//学习战胜多少人
	public static final String USER_SCORE_OVER = "score_over";//考试战胜多少人

	
	
	/**
	 * QUESTION AND share
	 * */
	public static final String PUBLISH_QUSETION_SHARE = "questionAndShare";//问答分享
	public static final String PUBLISH_QUSETION_SHARE_UID = "uid";//用户id
	public static final String PUBLISH_QUSETION_SHARE_TYPE = "type";//问答分享类型
	public static final String PUBLISH_QUSETION_SHARE_CONTENT = "content";//内容
	public static final String PUBLISH_QUSETION_SHARE_QID = "qid";//问答分享id
	public static final String PUBLISH_QUSETION_SHARE_PAGE_NO = "page_no";//加载的页码
	public static final String PUBLISH_QUSETION_SHARE_ITEM_PER_PAGE = "item_per_page";//每页加载的数量
	public static final String PUBLISH_QUSETION_SHARE_PICTURE = "picture";
	
	/**
	 * 本地资源更新
	 */
	public static final String CHK_UPDATA_PIC_NOTICE = "button_notice";//通知公告icon 
	public static final String CHK_UPDATA_PIC_TRAIN = "button_training";//微培训
	public static final String CHK_UPDATA_PIC_EXAM = "button_exam";//考试
	public static final String CHK_UPDATA_PIC_SURVEY = "button_survey";//培训调研
	public static final String CHK_UPDATA_PIC_TASK = "button_task";//任务签到
	public static final String CHK_UPDATA_PIC_CHATTER = "button_chatter";//同事圈
	public static final String CHK_UPDATA_PIC_ASK = "button_ask";// 问答
	public static final String CHK_UPDATA_PIC_COMPANY_LOGO = "button_vlogo";//主页logo
	public static final String CHK_UPDATA_PIC_NEWVER = "newver";//是否有更新
	public static final String CHK_UPDATA_BANNER = "banner";//首页轮播图信息
	
}
