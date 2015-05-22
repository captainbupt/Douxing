package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 任务签到实体类
 */
public class Task extends Category {

    public static final int CATEGORY_TYPE = Category.CATEGORY_TASK;
    public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//考试 的 未读数量

    public static final String TASK_FRAGMENT_ITEM_POSITION = "task_position";
    public static final String SIGN_BACK_TASK_FRAGMENT = "s2task";

    public int overdue;// 是否过期
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
        this.overdue = jsonObject.optInt(ResponseParams.OFFLINE);
        this.type = jsonObject.optInt(ResponseParams.TASK_DETAIL_TYPE);
        this.comment = jsonObject.optString(ResponseParams.TASK_DETAIL_COMMENT);
        this.startline = jsonObject.optLong(ResponseParams.TASK_DETAIL_STARTLINE) * 1000;
        this.img = jsonObject.optString(ResponseParams.IMG);
        this.photo = jsonObject.optInt(ResponseParams.TASK_PHOTO);
        this.read = jsonObject.optInt("read");
        try {
            this.latitude = jsonObject.getDouble(ResponseParams.TASK_DETAIL_LATITUDE);
            this.longitude = jsonObject.getDouble(ResponseParams.TASK_DETAIL_LONGITUDE);
        } catch (JSONException e1) {
            this.latitude = 0;
            this.longitude = 0;
            e1.printStackTrace();
        }
        String contentStr = jsonObject.optString(ResponseParams.CONTENT);
        this.place = jsonObject.optString(ResponseParams.TASK_DETAIL_PLACE);

        if (null != contentStr && !"".equals(contentStr)) {
            try {
                JSONObject jsonContent = new JSONObject(contentStr);
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

    public boolean isFinish() {
        if (read == Constant.FINISH_YES) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isOverdue() {
        if (overdue == Constant.OVERDUE_YES) {
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

    /**
     * 功能描述:  获取缓存
     */
    public static boolean getUnreadNum(Context context) {
        String userNum = ((AppApplication) context.getApplicationContext()).getUserInfo().getUserNumber();
        String sp = SP.getStringSP(context, SP.DEFAULTCACHE, userNum + CATEGORY_KEY_UNREAD_NUM, "");
        if (TextUtils.isEmpty(sp)) {
            return false;
        }
        try {
            JSONArray resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray
                        .optJSONObject(i);
                Task entity = new Task(jsonObject);
                if (!entity.isFinish()) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
