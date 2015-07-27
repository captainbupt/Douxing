package com.badou.mworking.entity.chatter;

import android.content.ContentValues;

import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述:  同事圈实体类
 */
public class Chatter implements Serializable {
    @SerializedName("qid")
    String qid;//qid
    @SerializedName("uid")
    String uid;
    @SerializedName("department")
    String department;
    @SerializedName("employee_id")
    String name;//员工号 (登录号? 用户名)
    @SerializedName("imgurl")
    String headUrl;//头像地址
    @SerializedName("content")
    String content;//发布内容
    @SerializedName("circle_lv")
    int level;
    @SerializedName("delop")
    int deletable;    // 同事圈中的该条信息是否可被删除
    @SerializedName("whom")
    String whom = "";    //私信人的电话号码
    @SerializedName("publish_ts")
    long publishTime;//发布时间
    @SerializedName("reply_no")
    int replyNumber;//评论数
    @SerializedName("credit_no")
    int praiseNumber = 0;//点赞数
    @SerializedName("photos")
    List<String> photoUrls;//内容中图片地址
    @SerializedName("picurl")
    String imgUrl; // 首张图片，主要是视频的缩略图
    @SerializedName("videourl")
    String videoUrl;    //视屏下载地址
    @SerializedName("store")
    boolean isStore;
    @SerializedName("weburl")
    LinkedTreeMap urlContentMap; // 为空时是一个array，会解析错误，这里判断一下

    transient UrlContent urlContent;

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable ? 1 : 0;
    }

    public String getQid() {
        return qid;
    }

    public String getDepartment() {
        return department;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getContent() {
        return content;
    }

    public int getLevel() {
        return level;
    }

    public boolean isDeletable() {
        return deletable == 1;
    }

    public String getWhom() {
        return whom;
    }

    public long getPublishTime() {
        return publishTime * 1000l;
    }

    public int getReplyNumber() {
        return replyNumber;
    }

    public int getPraiseNumber() {
        return praiseNumber;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public boolean isStore() {
        return isStore;
    }

    public void increasePraise() {
        praiseNumber++;
    }

    public void decreasePraise() {
        praiseNumber--;
    }

    public void setIsStore(boolean isStore) {
        this.isStore = isStore;
    }

    public boolean hasImageList() {
        return photoUrls != null && photoUrls.size() > 0;
    }

    public UrlContent getUrlContent() {
        if (urlContentMap != null && urlContentMap.containsKey("url") && urlContent == null) {
            urlContent = GsonUtil.fromJson(GsonUtil.toJson(urlContentMap, LinkedTreeMap.class), UrlContent.class);
        }
        return urlContent;
    }

    public ContentValues getValues() {
        ContentValues v = new ContentValues();
        v.put(MTrainingDBHelper.QUAN_QID, qid);
        v.put(MTrainingDBHelper.QUAN_IS_CHECK, 1);
        return v;
    }
}
