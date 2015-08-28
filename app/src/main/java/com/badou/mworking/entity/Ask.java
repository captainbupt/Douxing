package com.badou.mworking.entity;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 问答实体类
 */
public class Ask implements Serializable, StoreItem {

    @SerializedName("aid")
    String aid;
    @SerializedName("eid")
    String userName;       // 姓名
    @SerializedName("count")
    String count;  //回答或者点赞人数
    @SerializedName("create_ts")
    long createTime;     //创建时间
    @SerializedName("content")
    String content; //内容
    @SerializedName("imgurl")
    String userHeadUrl;  //头像地址
    @SerializedName("picurl")
    String contentImageUrl; //问题 图片地址
    @SerializedName("whom")
    String whom;    //私信聊天whom
    @SerializedName("circle_lv")
    int userLevel; //等级
    @SerializedName("delop")
    int isDeletable; // 是否有删除权限
    @SerializedName("subject")
    String subject; // 标题
    @SerializedName("store")
    boolean isStore;
    @SerializedName("uid")
    String uid;

    public String getUid() {
        return uid;
    }

    public String getAid() {
        return aid;
    }

    public String getUserName() {
        return userName;
    }

    public int getCount() {
        return Integer.parseInt(count);
    }

    public long getCreateTime() {
        return createTime * 1000l;
    }

    public String getContent() {
        return content;
    }

    public String getUserHeadUrl() {
        return userHeadUrl;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public String getWhom() {
        return whom;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public boolean isDeletable() {
        return isDeletable == 1;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isStore() {
        return isStore;
    }

    public void setStore(boolean isStore) {
        this.isStore = isStore;
    }

    public void increaseCount() {
        count = Integer.parseInt(count) + 1 + "";
    }
}
