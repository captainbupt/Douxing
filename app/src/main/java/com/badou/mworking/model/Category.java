package com.badou.mworking.model;

import com.badou.mworking.net.ResponseParams;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 功能描述:  通知公告，在线考试，微培训，任务签到分类信息
 */
public abstract class Category implements Serializable {

    public static final int CATEGORY_NOTICE = 0;     //通知公告
    public static final int CATEGORY_EXAM = 1;        //在线考试
    public static final int CATEGORY_TASK = 2;        //任务签到
    public static final int CATEGORY_TRAIN = 3;   //微培训
    public static final int CATEGORY_SHELF = 4;   //橱窗

    public static final String[] CATEGORY_KEY_NAMES = new String[]{"notice", "exam", "task", "training", "shelf"};
    public static final String[] CATEGORY_KEY_UNREADS = new String[]{"noticeUnreadNum", "examUnreadNum", "taskUnreadNum", "trainUnreadNum", "shelfUnreadNum"};

    public int tag; // 类别id
    public String subject; // 标题
    public String rid; // 资源唯一标识 主键id ，资源id
    public String department;// 部门
    public int top; // 是否置顶 top 默认为0，1表示置顶
    public long time; // 发布时间
    public String url; // 资源url 对应不同类型
    public int subtype; // 资源类型

    public Category(JSONObject jsonObject) {
        this.rid = jsonObject.optString(ResponseParams.CATEGORY_RID);
        this.subject = jsonObject.optString(ResponseParams.CATEGORY_SUBJECT);
        this.department = jsonObject
                .optString(ResponseParams.CATEGORY_DEPARTMENT);
        this.time = jsonObject.optLong(ResponseParams.CATEGORY_TIME) * 1000;
        this.top = jsonObject.optInt(ResponseParams.CATEGORY_TOP);
        this.tag = jsonObject
                .optInt(ResponseParams.CATEGORY_TAG);
        this.url = jsonObject.optString(ResponseParams.CATEGORY_URL);
        this.subtype = jsonObject.optInt(ResponseParams.CATEGORY_SUBTYPE);
    }

    public abstract int getCategoryType();

    public abstract String getCategoryKeyName();

    public abstract String getCategoryKeyUnread();

}
