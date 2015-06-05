package com.badou.mworking.model.category;

import android.text.TextUtils;

import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 任务签到实体类
 */
public class Task extends Category {

    public static final int CATEGORY_TYPE = CATEGORY_TASK;
    public static final String CATEGORY_KEY_NAME = CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public static final String CATEGORY_KEY_UNREAD_NUM = CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//考试 的 未读数量

    public static final String TASK_FRAGMENT_ITEM_POSITION = "task_position";
    public static final String SIGN_BACK_TASK_FRAGMENT = "s2task";

    public int offline;// 是否过期
    public int type;// TYPE
    public int photo;// 是否上传照片
    public int read;  //是否已经完成签到         1代表已经签到

    public long startline;// 开始时间
    public long deadline;// 结束时间

    public double longitude;// 经度
    public double latitude;// 纬度

    public String photoUrl;// 图片地址
    public String place;// 显示的地址
    public String img;
    public String comment;// 描述

    public Task(JSONObject jsonObject) {
        super(jsonObject);
        this.offline = jsonObject.optInt(ResponseParams.TASK_OFFLINE);
        this.type = jsonObject.optInt(ResponseParams.TASK_DETAIL_TYPE);
        this.comment = jsonObject.optString(ResponseParams.TASK_DETAIL_COMMENT);
        this.startline = jsonObject.optLong(ResponseParams.TASK_DETAIL_STARTLINE) * 1000;
        this.deadline = jsonObject.optLong(ResponseParams.TASK_DETAIL_DEADLINE) * 1000;
        this.img = jsonObject.optString(ResponseParams.TASK_IMG);
        this.photo = jsonObject.optInt(ResponseParams.TASK_PHOTO);
        this.latitude = jsonObject.optDouble(ResponseParams.TASK_DETAIL_LATITUDE);
        this.longitude = jsonObject.optDouble(ResponseParams.TASK_DETAIL_LONGITUDE);
        this.place = jsonObject.optString(ResponseParams.TASK_DETAIL_PLACE);

        String strContent = jsonObject.optString(ResponseParams.CONTENT);
        if (!TextUtils.isEmpty(strContent)) {
            try {
                JSONObject jsonContent = new JSONObject(strContent);
                this.photoUrl = jsonContent.optString(ResponseParams.P);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCategoryType() {
        return CATEGORY_TYPE;
    }

    @Override
    public String getCategoryKeyName() {
        return CATEGORY_KEY_NAME;
    }

    @Override
    public String getCategoryKeyUnread() {
        return CATEGORY_KEY_UNREAD_NUM;
    }

    public boolean isOffline() {
        if (offline == Constant.OVERDUE_YES) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isUpLoadPhoto() {
        if (photo == Constant.UPLOAD_PHOTO_YES) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFreeSign() {
        return latitude == 0 || longitude == 0;
    }
}
