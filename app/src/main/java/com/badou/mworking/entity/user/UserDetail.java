package com.badou.mworking.entity.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetail implements Serializable {

    @SerializedName("name")
    String name;//名字
    @SerializedName("dpt")
    String dpt;//部门
    @SerializedName("headimg")
    String headimg;//头像地址
    @SerializedName("score")
    int score;//分数
    @SerializedName("ask")
    int ask;// 我的圈帖子数量
    @SerializedName("share")
    int share;//分享
    @SerializedName("nmsg")
    int nmsg; //聊天未读数
    @SerializedName("circle_lv")
    int circle_lv; //等级
    @SerializedName("circle_rp")
    int circle_rp; //等级
    @SerializedName("training_total")
    int training_total;//培训总
    @SerializedName("training_week")
    int training_week;//培训周
    @SerializedName("study_total")
    int study_total;//学习总
    @SerializedName("study_week")
    int study_week;//学习周
    @SerializedName("study_rank")
    int study_rank;//学习排名
    @SerializedName("score_rank")
    int score_rank; //考试排名
    @SerializedName("study_over")
    int study_over; //学习战胜多少人
    @SerializedName("score_over")
    int score_over; //考试战胜了多少人
    @SerializedName("store")
    int store;

    public String getName() {
        return name;
    }

    public String getDpt() {
        return dpt;
    }

    public String getHeadimg() {
        return headimg;
    }

    public int getScore() {
        return score;
    }

    public int getAsk() {
        return ask;
    }

    public int getShare() {
        return share;
    }

    public int getNmsg() {
        return nmsg;
    }

    public int getLevel() {
        return circle_lv;
    }

    public int getCredit() {
        return circle_rp;
    }

    public int getTrainingTotal() {
        return training_total;
    }

    public int getTrainingWeek() {
        return training_week;
    }

    public int getStudyTotal() {
        return study_total;
    }

    public int getStudyWeek() {
        return study_week;
    }

    public int getStudyRank() {
        return study_rank;
    }

    public int getScoreRank() {
        return score_rank;
    }

    public int getStudyOver() {
        return study_over;
    }

    public int getScoreOver() {
        return score_over;
    }

    public int getStore() {
        return store;
    }
}
