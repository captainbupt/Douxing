package com.badou.mworking.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.MessageCenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/15.
 */
public class MessageCenterResManager {

    /**
     * 数据库中添加一条消息
     */
    public static void insertItem(Context context, MessageCenter message) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext())
                .getUserInfo().account;
        dbWriter.insert(MTrainingDBHelper.TBL_NAME_MESSAGE_CENTER + userNum.replace("@", ""), null, message.getContentValue());
        mTrainingDBHelper.closeDatabase();
    }

    public static List<Object> getAllItem(Context context) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext())
                .getUserInfo().account;
        List<Object> messageCenterList = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_MESSAGE_CENTER + userNum.replace("@", ""),
                null, null, null, null, null, MTrainingDBHelper.MESSAGE_CENTER_TS + " DESC");
        while (cursor.moveToNext()) {
            messageCenterList.add(new MessageCenter(cursor));
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return messageCenterList;
    }

    public static void deleteItem(Context context, MessageCenter message) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper
                .getMTrainingDBHelper();
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext())
                .getUserInfo().account;
        dbWriter.delete(MTrainingDBHelper.TBL_NAME_MESSAGE_CENTER + userNum.replace("@", ""), MTrainingDBHelper.PRIMARY_ID + "= ?", new String[]{message.id + ""});
        mTrainingDBHelper.closeDatabase();
    }
}
