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
    public int commentNum;

    public int hasFeedback = Constant.LIKED_NO;    // 是否点赞， 默认没有点赞

    /**
     * 功能描述:  train json 串解析， 得到train实体类对象
     *
     * @param jsonObject
     */
    public Train(JSONObject jsonObject) {
        super(jsonObject);
        this.imgUrl = jsonObject.optString(ResponseParams.KNOWLEDGE_LIBRARY_IMG);

        // 直接optJSONObject只会返回null
        String strContent = jsonObject.optString(ResponseParams.CONTENT);
        if (!TextUtils.isEmpty(strContent)) {
            try {
                JSONObject contentJsonObject = new JSONObject(strContent);
                this.hasFeedback = contentJsonObject.optInt(ResponseParams.M);
                this.coursewareScore = contentJsonObject.optString(ResponseParams.E);
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

}
