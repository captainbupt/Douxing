package com.badou.mworking.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.WenDaAnswer;

public class WenDaManage {

	/**
	 * 数据库中添加一条已经点赞过的qid数据
	 * @param context
	 * @param question
	 */
	public static void insertItem(Context context,WenDaAnswer WenDaAnswer) {
		MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
				.getMTrainingDBHelper();
		SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
		String userNum = ((AppApplication) context.getApplicationContext())
				.getUserInfo().account;
		try {
			dbReader.insert(MTrainingDBHelper.TBL_NAME_WENDADIANZAN + userNum, null, WenDaAnswer.getValues());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			mTrainingDBHelper.closeDatabase();
		}
	}

	
	/**
	 * 查询数据库中是否有此条数据
	 * @param context
	 * @param qid
	 * @return 有(true) 无(false)
	 */
	public static boolean isSelect(Context context ,String qid,String create_ts) {
		boolean flag = false;
		MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
				.getMTrainingDBHelper();
		SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
		String userNum = ((AppApplication) context.getApplicationContext())
				.getUserInfo().account;
		try {
			Cursor c1 = dbReader.query(MTrainingDBHelper.TBL_NAME_WENDADIANZAN + userNum, 
					null, 
					MTrainingDBHelper.WENDA_QID + " = ? ", 
					new String[]{qid.trim()+create_ts.trim()},
					null, 
					null, 
					null);
				 while (c1.moveToNext()){
					 flag = true;
					 return flag;
				 };
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			mTrainingDBHelper.closeDatabase();
		}
		return flag;
		
	}
}
