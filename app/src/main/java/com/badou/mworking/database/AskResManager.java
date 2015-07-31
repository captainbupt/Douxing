package com.badou.mworking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.entity.user.UserInfo;

public class AskResManager {

    /**
     * 数据库中添加一条已经点赞过的qid数据
     */
    public static void insertItem(String aid, long createTime) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MTrainingDBHelper.WENDA_QID, aid.trim() + createTime / 1000l);

        try {
            dbReader.insert(MTrainingDBHelper.TBL_NAME_WENDADIANZAN + userNum.replace("@", ""), null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mTrainingDBHelper.closeDatabase();
        }
    }


    /**
     * 查询数据库中是否有此条数据
     *
     * @param qid
     * @return 有(true) 无(false)
     */
    public static boolean isSelect(String qid, long create_ts) {
        boolean flag = false;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        try {
            Cursor c1 = dbReader.query(MTrainingDBHelper.TBL_NAME_WENDADIANZAN + userNum.replace("@", ""),
                    null,
                    MTrainingDBHelper.WENDA_QID + " = ? ",
                    new String[]{qid.trim() + create_ts / 1000l},
                    null,
                    null,
                    null);
            if (c1.moveToFirst()) {
                flag = true;
            }
            c1.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mTrainingDBHelper.closeDatabase();
        }
        return flag;

    }
}
