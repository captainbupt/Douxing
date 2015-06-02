package com.badou.mworking.model;

import android.text.TextUtils;

import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 功能描述: 考试实体类
 */
public class Exam extends Category {

    public static final int CATEGORY_TYPE = Category.CATEGORY_EXAM;
    public static final String CATEGORY_KEY_NAME = Category.CATEGORY_KEY_NAMES[CATEGORY_TYPE];
    public static final String CATEGORY_KEY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[CATEGORY_TYPE];//考试 的 未读数量

    public static final int GRADED_FINISH = 1;     // 批阅完成

    public int score = 0;     //得分，默认为0
    public int pass = 0;   // 该门课的及格分数，大于等于该分数，就代表及格了
    public int total = 0;  // 考试总分
    public boolean isGraded = false; //包含在content中，是否已批阅       ，done == 1代表已完成， done==0代表待批阅
    public int offline;//是否过期

    public String url; //试卷的下载地址
    public String credit; //学分


    public Exam(JSONObject jsonObject) {
        super(jsonObject);
/*        if (jsonObject.has(ResponseParams.CATEGORY_SUBTYPE)) {
            this.subtype = jsonObject.optInt(ResponseParams.CATEGORY_SUBTYPE);
        } else {
            this.subtype = Constant.MWKG_FORAMT_TYPE_XML;
        }*/
        this.offline = jsonObject.optInt(ResponseParams.EXAM_OFFLINE);
        this.credit = jsonObject.optString(ResponseParams.EXAM_CREDIT);
        this.pass = jsonObject.optInt(ResponseParams.EXAM_PASS);

        // 直接optJSONObject只会返回null
        String strContent = jsonObject.optString(ResponseParams.EXAM_CONTENT);
        if (!TextUtils.isEmpty(strContent)) {
            try {
                JSONObject examContent = null;
                examContent = new JSONObject(strContent);
                this.score = examContent.optInt(ResponseParams.EXAM_S);
                this.total = examContent.optInt(ResponseParams.EXAM_T);
                this.isGraded = examContent.optInt(ResponseParams.EXAM_D) == GRADED_FINISH;
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

    public boolean isRead() {
        if (this.read == 1) {
            return true;
        } else {
            return false;
        }
    }
}
