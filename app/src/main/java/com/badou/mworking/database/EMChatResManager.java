package com.badou.mworking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.emchat.Department;
import com.badou.mworking.model.emchat.Role;
import com.easemob.chatuidemo.domain.User;

import java.util.ArrayList;
import java.util.List;

public class EMChatResManager {

    // 不存在修改操作，每次插入必然全部替换
    public static void insertDepartments(Context context, List<Department> departments) {
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        mTrainingDBHelper.clear(MTrainingDBHelper.TBL_NAME_EMCHAT_DEPARTMENT + userNum.replace("@", ""));
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        for (Department department : departments) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MTrainingDBHelper.PRIMARY_ID, department.getId());
            contentValues.put(MTrainingDBHelper.EMCHAT_DEPARTMENT_NAME, department.getName());
            contentValues.put(MTrainingDBHelper.EMCHAT_DEPARTMENT_PARENT, department.getParent());
            contentValues.put(MTrainingDBHelper.EMCHAT_DEPARTMENT_SON, department.getSonString());
            dbWriter.insert(MTrainingDBHelper.TBL_NAME_EMCHAT_DEPARTMENT + userNum.replace("@", ""), null, contentValues);
        }
        mTrainingDBHelper.closeDatabase();
    }

    public static List<Department> getDepartments(Context context) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        List<Department> departments = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_DEPARTMENT + userNum.replace("@", ""),
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.PRIMARY_ID));
            String name = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_NAME));
            String sons = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_SON));
            long parent = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_PARENT));
            departments.add(new Department(id, name, parent, sons));
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return departments;
    }

    public static void insertRoles(Context context, List<Role> roles) {
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        mTrainingDBHelper.clear(MTrainingDBHelper.TBL_NAME_EMCHAT_ROLE + userNum.replace("@", ""));
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        for (Role role : roles) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MTrainingDBHelper.PRIMARY_ID, role.getId());
            contentValues.put(MTrainingDBHelper.EMCHAT_ROLE_NAME, role.getName());
            dbWriter.insert(MTrainingDBHelper.TBL_NAME_EMCHAT_ROLE + userNum.replace("@", ""), null, contentValues);
        }
        mTrainingDBHelper.closeDatabase();
    }

    public static List<Role> getRoles(Context context) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        List<Role> roles = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_ROLE + userNum.replace("@", ""),
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MTrainingDBHelper.PRIMARY_ID));
            String name = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_ROLE_NAME));
            roles.add(new Role(id, name));
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return roles;
    }

    public static void insertContacts(Context context, List<User> contacts) {
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        mTrainingDBHelper.clear(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""));
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        for (User contact : contacts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MTrainingDBHelper.EMCHAT_USER_NAME, contact.getUsername());
            contentValues.put(MTrainingDBHelper.EMCHAT_NICK_NAME, contact.getNick());
            contentValues.put(MTrainingDBHelper.EMCHAT_DEPARTMENT, contact.getDepartment());
            contentValues.put(MTrainingDBHelper.EMCHAT_ROLE, contact.getRole());
            contentValues.put(MTrainingDBHelper.EMCHAT_IMG_URL, contact.getAvatar());
            dbWriter.insert(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""), null, contentValues);
        }
        mTrainingDBHelper.closeDatabase();
    }

    public static List<User> getContacts(Context context) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().account;
        List<User> departments = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""),
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setUsername(cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_USER_NAME)));
            user.setNick(cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_NICK_NAME)));
            user.setDepartment(cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT)));
            user.setRole(cursor.getInt(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_ROLE)));
            user.setAvatar(cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_IMG_URL)));
            user.setHeader(String.valueOf(user.getNick().charAt(0)));
            departments.add(user);
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return departments;
    }

}
