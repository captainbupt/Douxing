package com.badou.mworking.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class MTrainingDBHelper extends SQLiteOpenHelper {
	
	private AtomicInteger mOpenCounter = new AtomicInteger();

	private SQLiteDatabase mDatabase;

	private static final String DB_NAME = "coll.db";
	private static final int DB_VERSION = 1;       //数据库版本
	public static final String TBL_NAME_TONG_SHI_QUAN = "credit";// 同事圈 表名
	public static final String TBL_NAME_WENDADIANZAN = "wendadianzan";// 问答点赞
	public static final String PRIMARY_ID = "_id";//id
	public static final String RESOURCE_ID = "rid";//id
	public static final String RESOURCE_TIME = "ts";//时间戳
	public static final String TOP = "top"; // 是否置顶
	public static final String URL ="url";//网页地址

	public static final String EXAM_SUBJECT = "subject";//标题
	public static final String EXAM_DEPARTMENT = "department";//部门
	public static final String EXAM_TYPE = "subtype";//模块类型
	public static final String EXAM_ID_CONTENT = "content";//内容
	public static final String _TAG = "tag";
	public static final String OFFLINE = "offline";//总数

	
	public static final String NOTICE_SUBJECT = "subject";//标题
	public static final String NOTICE_DEPARTMENT = "department";//部门
	public static final String NOTICE_URL = "url";//通知的网页
	public static final String NOTICE_TYPE = "subtype";//模块类别
	public static final String NOTICE_HAS_READ = "state";//是否已读
	public static final String NOTICE_TAG = "tag";//列表类别
	
	public static final String TASK_SUBJECT = "subject";//标题
	public static final String TASK_DETAIL_DEPARTMENT = "detaildepartment";//详细部门
	public static final String TASK_DETAIL_TYPE = "type";//模块类别
	public static final String TASK_DETAIL_LONGITUDE = "longitude";//经度
	public static final String TASK_DETAIL_LATITUDE = "latitude";//纬度
	public static final String TASK_DETAIL_PLACE = "place";//位置
	public static final String TASK_DETAIL_COMMENT = "comment";//评论
	public static final String TASK_DETAIL_STARTLINE = "startline";//经度
	public static final String TASK_DETAIL_DEADLINE = "deadline";//截止时间
	public static final String TASK_PHOTO = "photo";  //是否需要上传图片
	public static final String TASK_PHOTO_URL = "photo_url";  //显示图片的url
	
	public static final String CHK_IMG = "img";// 图片url

	/**
	 * categroy
	 */
	public static final String CATE_TAG = "tag";//tag
	
	/**
	 * 同事圈的字段
	 */
	public static final String QUAN_IS_CHECK = "state";//是否点赞(int)
	public static final String QUAN_QID = "qid";//哪一个被点赞啦
	
	public static final String WENDA_QID = "qid";// 问答aid + ts 字段
	
	public MTrainingDBHelper(Context c) {
		super(c, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	public void createUserTable(String userNum) {
		SQLiteDatabase db = getDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_TONG_SHI_QUAN + userNum + " ( "
				+ PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ QUAN_QID + " TEXT, " + QUAN_IS_CHECK + " INTEGER )");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_WENDADIANZAN + userNum + " ( "
				+ PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ WENDA_QID + " TEXT )");
		mTrainingDBHelper.closeDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}


	//单例模式获取数据库操作对象
	private static MTrainingDBHelper mTrainingDBHelper;

	/**
	 * 功能描述:  数据库初始化
	 * @param context
	 */
	public static void init(Context context) {
		mTrainingDBHelper = new MTrainingDBHelper(context);
	}

	/**
	 * 功能描述:  获取数据库操作DBHelper对象
	 * @return
	 */
	public static MTrainingDBHelper getMTrainingDBHelper() {
		if (mTrainingDBHelper != null) {
			return mTrainingDBHelper;
		} else {
			throw new IllegalStateException("MTrainingDBHelper 初始化失败");
		}
	}
	
	/**
	 * 功能描述:  获取可写的数据库操作对象   
	 * @return
	 */
	public synchronized SQLiteDatabase getDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = mTrainingDBHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	/**
	 * 功能描述: 关闭数据库
	 */
	public synchronized void closeDatabase() {
     
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();
		}
	}
	
	/**
	 * 功能描述:  删除表中数据
	 * @param tableName   表名
	 * @param column        需要匹配的表的字段    数据库表中id字段
	 * @param id    需要匹配的表的字段 的数据     该条数据id
	 */
	public void del(String tableName, String column, String id) {
		SQLiteDatabase db = getDatabase();
		db.delete(tableName, column + "=?", new String[] { id + "" });
		mTrainingDBHelper.closeDatabase();
	}

	/**
	 * 功能描述:   通过表名删除数据库表
	 * @param tableName   表名
	 */
	public void dropTable(String tableName) {
		try {
			SQLiteDatabase db = getDatabase();
			db.execSQL("DROP TABLE " + tableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mTrainingDBHelper.closeDatabase();
	}
	
}
