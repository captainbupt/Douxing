package com.badou.mworking.entity.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 考试实体类
 */
public class Exam extends Category {

    public final int CATEGORY_TYPE = CATEGORY_EXAM;
    public final String CATEGORY_KEY_NAME = CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public final String CATEGORY_KEY_UNREAD_NUM = CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//考试 的 未读数量

    public static final int GRADED_FINISH = 1;     // 批阅完成

    public int score = 0;     //得分，默认为0
    public int pass = 0;   // 该门课的及格分数，大于等于该分数，就代表及格了
    public int total = 0;  // 考试总分
    public boolean isGraded = false; //包含在content中，是否已批阅       ，done == 1代表已完成， done==0代表待批阅
    public boolean isOffline;//是否过期

    public String credit; //学分

    public Exam() {
    }

    public Exam(Context context, JSONObject jsonObject) {
        super(context, jsonObject);
/*        if (jsonObject.has(ResponseParams.CATEGORY_SUBTYPE)) {
            this.subtype = jsonObject.optInt(ResponseParams.CATEGORY_SUBTYPE);
        } else {
            this.subtype = Constant.MWKG_FORAMT_TYPE_XML;
        }*/
        this.isOffline = jsonObject.optInt(ResponseParameters.EXAM_OFFLINE) == Constant.OVERDUE_YES;
        this.credit = jsonObject.optString(ResponseParameters.EXAM_CREDIT);
        this.pass = jsonObject.optInt(ResponseParameters.EXAM_PASS);
        String uid = UserInfo.getUserInfo().getUid();
        this.url = Net.getRunHost(context) + Net.EXAM_ITEM(uid, rid);

        // 直接optJSONObject只会返回null
        String strContent = jsonObject.optString(ResponseParameters.EXAM_CONTENT);
        if (!TextUtils.isEmpty(strContent)) {
            try {
                JSONObject examContent = null;
                examContent = new JSONObject(strContent);
                this.score = examContent.optInt(ResponseParameters.EXAM_CONTENT_SCORE);
                this.total = examContent.optInt(ResponseParameters.EXAM_CONTENT_TOTAL);
                this.isGraded = examContent.optInt(ResponseParameters.EXAM_CONTENT_GRADED) == GRADED_FINISH;
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

    @Override
    public boolean isAvailable() { // 是否可以签到（未读且未过期）
        return !isRead && !isOffline;
    }

}
