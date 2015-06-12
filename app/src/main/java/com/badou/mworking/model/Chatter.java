package com.badou.mworking.model;

import android.content.ContentValues;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.net.ResponseParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:  同事圈实体类
 */
public class Chatter implements Serializable {

    public String qid;//qid
    public String uid;
    public String name;//员工号 (登录号? 用户名)
    public String department;
    public String headUrl;//头像地址
    public String content;//发布内容
    public int level;
    public boolean deletable;    // 同事圈中的该条信息是否可被删除
    public String whom = "";    //私信人的电话号码
    public long publishTime;//发布时间
    public int replyNumber;//评论数
    public int praiseNumber = 0;//点赞数

    public List<Object> photoUrls;//内容中图片地址
    public String imgUrl;
    public String videoUrl;    //视屏下载地址

    public Chatter(JSONObject jsonObject) {
        uid = jsonObject.optString(ResponseParameters.USER_ID);
        qid = jsonObject.optString(ResponseParameters.QUESTION_QID);
        name = jsonObject.optString(ResponseParameters.QUESTION_EMPLOYEE_ID);
        department = jsonObject.optString(ResponseParameters.QUESTION_DEPARTMENT);
        content = jsonObject.optString(ResponseParameters.QUESTION_CONTENT);
        headUrl = jsonObject.optString(ResponseParameters.QUESTION_IMG_URL);
        level = jsonObject.optInt(ResponseParameters.QUESTION_CIRCLE_LV);
        deletable = jsonObject.optInt("delop") == 1;
        whom = jsonObject.optString(ResponseParameters.QUESTION_WHOM);
        publishTime = Long.parseLong(jsonObject
                .optString(ResponseParameters.QUESTION_PUBLISH_TS)) * 1000l;
        replyNumber = Integer.parseInt(jsonObject
                .optString(ResponseParameters.QUESTION_REPLY_NO));
        praiseNumber = jsonObject.optInt(ResponseParameters.QUESTION_CREDIT_NUM);
        JSONArray photoArray = jsonObject.optJSONArray(ResponseParameters.QUESTION_PHOTO_URL);
        photoUrls = new ArrayList<>();
        for (int ii = 0; ii < photoArray.length(); ii++) {
            photoUrls.add(photoArray.optString(ii));
        }
        imgUrl = jsonObject.optString(ResponseParameters.QUESTION_PICTURE_URL);
        videoUrl = jsonObject.optString(ResponseParameters.QUESTION_VIDEO_URL);
    }

    public ContentValues getValues() {
        ContentValues v = new ContentValues();
        v.put(MTrainingDBHelper.QUAN_QID, qid);
        v.put(MTrainingDBHelper.QUAN_IS_CHECK, 1);
        return v;
    }
}
