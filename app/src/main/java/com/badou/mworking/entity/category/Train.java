package com.badou.mworking.entity.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 培训实体类
 */
public class Train extends Category {

    public int CATEGORY_TYPE;
    public String CATEGORY_KEY_NAME;
    public String CATEGORY_KEY_UNREAD_NUM;//训练 的 未读数量

    public String imgUrl;//图片下载地址
    public int rating; //课件打分

    public int ecnt; //评分人数
    public int eval; //评分总分
    public int commentNum;
    public boolean isTraining = true;

    public int hasFeedback = Constant.LIKED_NO;    // 是否点赞， 默认没有点赞

    public Train(boolean isTraining) {
        if (isTraining) {
            CATEGORY_TYPE = CATEGORY_TRAINING;
        } else {
            CATEGORY_TYPE = CATEGORY_SHELF;
        }
        CATEGORY_KEY_NAME = CATEGORY_KEY_NAMES[CATEGORY_TYPE];
        CATEGORY_KEY_UNREAD_NUM = CATEGORY_KEY_UNREADS[CATEGORY_TYPE];
    }


    /**
     * 功能描述:  train json 串解析， 得到train实体类对象
     *
     * @param jsonObject
     */
    public Train(Context context, JSONObject jsonObject, boolean isTraining) {
        super(context, jsonObject);
        this.isTraining = isTraining;
        this.imgUrl = jsonObject.optString(ResponseParameters.KNOWLEDGE_LIBRARY_IMG);

        // 直接optJSONObject只会返回null
        String strContent = jsonObject.optString(ResponseParameters.CONTENT);
        if (!TextUtils.isEmpty(strContent)) {
            try {
                JSONObject contentJsonObject = new JSONObject(strContent);
                this.hasFeedback = contentJsonObject.optInt(ResponseParameters.M);
                this.rating = Integer.parseInt(contentJsonObject.optString(ResponseParameters.E));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isTraining) {
            CATEGORY_TYPE = CATEGORY_TRAINING;
        } else {
            CATEGORY_TYPE = CATEGORY_SHELF;
        }
        CATEGORY_KEY_NAME = CATEGORY_KEY_NAMES[CATEGORY_TYPE];
        CATEGORY_KEY_UNREAD_NUM = CATEGORY_KEY_UNREADS[CATEGORY_TYPE];
    }

    public Train() {

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
