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
 * 功能描述: 培训实体类
 */
public class Train extends Category {

    public static final int CATEGORY_TYPE = Category.CATEGORY_TRAIN;
    public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//训练 的 未读数量

    public String imgUrl;//图片下载地址
    public String coursewareScore = ""; //课件打分

    public int ecnt; //评分人数
    public int eval; //评分总分

    public int isRead;        //是否已读（通知公告，为培训标示是否已读）
    public int hasFeedback = Constant.LIKED_NO;    // 是否点赞， 默认没有点赞

    /**
     * 功能描述:  train json 串解析， 得到train实体类对象
     *
     * @param jsonObject
     */
    public Train(JSONObject jsonObject) {
        super(jsonObject);
        this.isRead = jsonObject.optInt(ResponseParams.TRAIN_READ);
        this.imgUrl = jsonObject.optString(ResponseParams.KNOWLEDGE_LIBRARY_IMG);
        JSONObject contentJsonObject = jsonObject.optJSONObject(ResponseParams.CONTENT);
        if (contentJsonObject != null) {
            this.hasFeedback = contentJsonObject.optInt(ResponseParams.M);
            this.coursewareScore = contentJsonObject.optString(ResponseParams.E);
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
                Train entity = new Train(jsonObject);
                if (1 == entity.isRead) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
