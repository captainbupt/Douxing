package com.badou.mworking.entity;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 问答实体类
 */
public class Ask implements Serializable {

    public final static String WENDACACHE = "wendaCache";

    public String aid;
    public String userName;       // 姓名
    public int count;  //回答或者点赞人数
    public Long createTime;     //创建时间
    public String content; //内容
    public String userHeadUrl;  //头像地址
    public String contentImageUrl; //问题 图片地址
    public String whom;    //私信聊天whom
    public int userLevel; //等级
    public boolean isDeletable; // 是否有删除权限
    public String subject; // 标题
    public boolean isStore;

    /**
     * 功能描述: json解析
     *
     * @param jsonObject
     */
    public Ask(JSONObject jsonObject) {
        super();
        System.out.println(jsonObject);
        this.content = jsonObject.optString("content");
        this.count = jsonObject.optInt("count");
        this.contentImageUrl = jsonObject.optString("picurl");
        this.aid = jsonObject.optString("aid");
        this.createTime = jsonObject
                .optLong("create_ts") * 1000;
        this.userName = jsonObject
                .optString("eid");
        this.userLevel = jsonObject
                .optInt("circle_lv");
        this.userHeadUrl = jsonObject
                .optString("imgurl");
        this.whom = jsonObject.optString("whom");
        this.isDeletable = jsonObject.optInt("delop", 0) == 1;
        this.subject = jsonObject.optString("subject");
        this.isStore = jsonObject.optBoolean("store");
    }
}
