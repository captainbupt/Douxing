package com.badou.mworking.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.entity.Chatter;
import com.badou.mworking.entity.user.UserInfo;

public class ChatterResManager {

    /**
     * 数据库中添加一条已经点赞过的qid数据
     *
     * @param context
     * @param question
     */
    public static void insertItem(Context context, Chatter question) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        try {

            dbWriter.insert(MTrainingDBHelper.TBL_NAME_TONG_SHI_QUAN + userNum.replace("@", ""), null, question.getValues());

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
    public static boolean isSelect(Context context, String qid) {
        boolean flag = false;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        try {

            Cursor c1 = dbReader.query(MTrainingDBHelper.TBL_NAME_TONG_SHI_QUAN  + userNum.replace("@", ""),
                    null,
                    MTrainingDBHelper.QUAN_QID + " = ? ",
                    new String[]{qid.trim()},
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
