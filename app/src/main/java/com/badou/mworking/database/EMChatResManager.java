package com.badou.mworking.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badou.mworking.entity.emchat.Department;
import com.badou.mworking.entity.emchat.Role;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.entity.emchat.User;

import java.util.ArrayList;
import java.util.List;

public class EMChatResManager {

    // 不存在修改操作，每次插入必然全部替换
    public static void insertDepartments(List<Department> departments) {
        String userNum = UserInfo.getUserInfo().getAccount();
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

    public static List<Department> getDepartments() {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
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

    public static List<Department> getChildDepartments(long parentId) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        List<Department> departments = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_DEPARTMENT + userNum.replace("@", ""),
                null, MTrainingDBHelper.EMCHAT_DEPARTMENT_PARENT + " = ?", new String[]{parentId + ""}, null, null, null);
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

    public static Department getDepartment(long id) {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_DEPARTMENT + userNum.replace("@", ""),
                null, MTrainingDBHelper.PRIMARY_ID + " = ?", new String[]{id + ""}, null, null, null);
        Department department = null;
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_NAME));
            String sons = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_SON));
            long parent = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT_PARENT));
            department = new Department(id, name, parent, sons);
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return department;
    }

    public static void insertRoles(List<Role> roles) {
        String userNum = UserInfo.getUserInfo().getAccount();
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

    public static List<Role> getRoles() {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
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

    public static void insertContacts(List<User> contacts) {
        String userNum = UserInfo.getUserInfo().getAccount();
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        mTrainingDBHelper.clear(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""));
        SQLiteDatabase dbWriter = mTrainingDBHelper.getDatabase();
        for (User contact : contacts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MTrainingDBHelper.EMCHAT_USER_NAME, contact.getUsername());
            contentValues.put(MTrainingDBHelper.EMCHAT_NICK_NAME, contact.getNick());
            contentValues.put(MTrainingDBHelper.EMCHAT_DEPARTMENT, contact.getDepartmentId());
            contentValues.put(MTrainingDBHelper.EMCHAT_ROLE, contact.getRole());
            contentValues.put(MTrainingDBHelper.EMCHAT_IMG_URL, contact.getAvatar());
            dbWriter.insert(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""), null, contentValues);
        }
        mTrainingDBHelper.closeDatabase();
    }

    public static List<User> getContacts() {
        MTrainingDBHelper mTrainingDBHelper = MTrainingDBHelper.getMTrainingDBHelper();
        SQLiteDatabase dbReader = mTrainingDBHelper.getDatabase();
        String userNum = UserInfo.getUserInfo().getAccount();
        List<User> users = new ArrayList<>();
        Cursor cursor = dbReader.query(MTrainingDBHelper.TBL_NAME_EMCHAT_USER + userNum.replace("@", ""),
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_USER_NAME));
            String nickName = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_NICK_NAME));
            long department = cursor.getLong(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_DEPARTMENT));
            int role = cursor.getInt(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_ROLE));
            String avatar = cursor.getString(cursor.getColumnIndex(MTrainingDBHelper.EMCHAT_IMG_URL));
            User user = new User(username, nickName, avatar, department, role);
            users.add(user);
        }
        cursor.close();
        mTrainingDBHelper.closeDatabase();
        return users;
    }

}
