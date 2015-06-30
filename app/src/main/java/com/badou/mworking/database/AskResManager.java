package com.badou.mworking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;

public class AskResManager {

    /**
     * 数据库中添加一条已经点赞过的qid数据
     *
     * @param context
     */
    public static void insertItem(Context context, String aid, long createTime) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MTrainingDBHelper.WENDA_QID, aid + (createTime / 1000));

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
     * @param context
     * @param qid
     * @return 有(true) 无(false)
     */
    public static boolean isSelect(Context context, String qid, String create_ts) {
        boolean flag = false;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        try {
            Cursor c1 = dbReader.query(MTrainingDBHelper.TBL_NAME_WENDADIANZAN + userNum.replace("@", ""),
                    null,
                    MTrainingDBHelper.WENDA_QID + " = ? ",
                    new String[]{qid.trim() + create_ts.trim()},
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
