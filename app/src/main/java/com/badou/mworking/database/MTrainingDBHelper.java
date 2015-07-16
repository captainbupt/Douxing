package com.badou.mworking.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class MTrainingDBHelper extends SQLiteOpenHelper {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private SQLiteDatabase mDatabase;
    // 不要随意更改或者添加删除列名，会导致升级时发生错误
    private static final String DB_NAME = "coll.db";
    // private static final int DB_VERSION = 1;       //数据库版本1.6.0
    private static final int DB_VERSION = 2;       //数据库版本1.6.2
    public static final String TBL_NAME_TONG_SHI_QUAN = "credit";// 同事圈 表名
    public static final String TBL_NAME_WENDADIANZAN = "wendadianzan";// 问答点赞
    public static final String TBL_NAME_MESSAGE_CENTER = "messagecenter";// 消息中心
    public static final String TBL_NAME_EMCHAT_DEPARTMENT = "emdpt";// 环信部门
    public static final String TBL_NAME_EMCHAT_ROLE = "emrole";// 环信岗位
    public static final String TBL_NAME_EMCHAT_USER = "emuser";// 环信好友
    public static final String PRIMARY_ID = "_id";//id

    public static final String QUAN_IS_CHECK = "state";//是否点赞(int)
    public static final String QUAN_QID = "qid";//哪一个被点赞啦

    public static final String WENDA_QID = "qid";// 问答aid + ts 字段

    public static final String MESSAGE_CENTER_TYPE = "type";
    public static final String MESSAGE_CENTER_DESCRIPTION = "description";
    public static final String MESSAGE_CENTER_ADD = "adds";
    public static final String MESSAGE_CENTER_TS = "ts";

    public static final String EMCHAT_DEPARTMENT_PARENT = "parent";
    public static final String EMCHAT_DEPARTMENT_NAME = "name";
    public static final String EMCHAT_DEPARTMENT_SON = "son";

    public static final String EMCHAT_ROLE_NAME = "name";

    public static final String EMCHAT_USER_NAME = "username";
    public static final String EMCHAT_NICK_NAME = "nickname";
    public static final String EMCHAT_SPELL = "spell";
    public static final String EMCHAT_DEPARTMENT = "department";
    public static final String EMCHAT_ROLE = "role";
    public static final String EMCHAT_IMG_URL = "imgurl";

    public MTrainingDBHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void createUserTable(String userNum) {
        SQLiteDatabase db = getDatabase();
        userNum = userNum.replace("@", "");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_TONG_SHI_QUAN + userNum + " ( "
                + PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + QUAN_QID + " TEXT, " + QUAN_IS_CHECK + " INTEGER )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_WENDADIANZAN + userNum + " ( "
                + PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WENDA_QID + " TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_MESSAGE_CENTER + userNum + " ( "
                + PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MESSAGE_CENTER_TYPE + " TEXT, " + MESSAGE_CENTER_DESCRIPTION + " TEXT, "
                + MESSAGE_CENTER_ADD + " TEXT, " + MESSAGE_CENTER_TS + " LONG )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_EMCHAT_DEPARTMENT + userNum + " ( "
                + PRIMARY_ID + " LONG PRIMARY KEY,"
                + EMCHAT_DEPARTMENT_NAME + " TEXT, " + EMCHAT_DEPARTMENT_PARENT + " LONG, "
                + EMCHAT_DEPARTMENT_SON + " TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_EMCHAT_ROLE + userNum + " ( "
                + PRIMARY_ID + " INTEGER PRIMARY KEY," + EMCHAT_ROLE_NAME + " TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_NAME_EMCHAT_USER + userNum + " ( "
                + EMCHAT_USER_NAME + " TEXT PRIMARY KEY,"
                + EMCHAT_NICK_NAME + " TEXT, " + EMCHAT_DEPARTMENT + " LONG, "
                + EMCHAT_ROLE + " INTEGER, " + EMCHAT_IMG_URL + " TEXT )");
        mTrainingDBHelper.closeDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    //单例模式获取数据库操作对象
    private static MTrainingDBHelper mTrainingDBHelper;

    /**
     * 功能描述:  数据库初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mTrainingDBHelper = new MTrainingDBHelper(context);
    }

    /**
     * 功能描述:  获取数据库操作DBHelper对象
     *
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
     *
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


    public void clear(String tableName) {
        String sql = "DELETE FROM " + tableName + ";";
        SQLiteDatabase db = getDatabase();
        db.execSQL(sql);
        revertSeq(tableName);
        closeDatabase();
    }

    private void revertSeq(String tableName) {
        String sql = "update sqlite_sequence set seq=0 where name='" + tableName + "'";
        SQLiteDatabase db = getDatabase();
        db.execSQL(sql);
        closeDatabase();
    }

    /**
     * 功能描述:  删除表中数据
     *
     * @param tableName 表名
     * @param column    需要匹配的表的字段    数据库表中id字段
     * @param id        需要匹配的表的字段 的数据     该条数据id
     */
    public void del(String tableName, String column, String id) {
        SQLiteDatabase db = getDatabase();
        db.delete(tableName, column + "=?", new String[]{id + ""});
        closeDatabase();
    }

    /**
     * 功能描述:   通过表名删除数据库表
     *
     * @param tableName 表名
     */
    public void dropTable(String tableName) {
        try {
            SQLiteDatabase db = getDatabase();
            db.execSQL("DROP TABLE " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDatabase();
    }
}
