package com.badou.mworking.entity.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 功能描述: 培训实体类
 */
public class Train extends Category {

    transient boolean isTraining = true;

    @Expose
    @SerializedName("commentInfo")
    TrainingCommentInfo commentInfo;
    @Expose
    @SerializedName("rating")
    int rating;

    public Train() {
        commentInfo = new TrainingCommentInfo();
    }

    public void updateData(CategoryDetail categoryDetail) {
        rating = categoryDetail.getContent().e;
        this.store = categoryDetail.store;
        this.read = 1;
        commentInfo.eval = categoryDetail.eval;
        commentInfo.mcnt = categoryDetail.mcnt;
        commentInfo.ccnt = categoryDetail.ccnt;
        commentInfo.ecnt = categoryDetail.ecnt;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getCategoryType() {
        return isTraining ? Category.CATEGORY_TRAINING : Category.CATEGORY_SHELF;
    }

    public void setIsTraining(boolean isTraining) {
        this.isTraining = isTraining;
    }

    public boolean isTraining() {
        return isTraining;
    }

    public void setCommentInfo(TrainingCommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public int getCommentNumber() {
        return commentInfo.ccnt;
    }

    public void setCommentNumber(int number) {
        commentInfo.ccnt = number;
    }

    public int getRatingNumber() {
        return commentInfo.ecnt;
    }

    public int getRatingTotalValue() {
        return commentInfo.eval;
    }

    public int getRating() {
        return rating;
    }

    public void setRatingValue(int rating, int number) {
        this.rating = rating;
        commentInfo.ecnt = number;
        commentInfo.eval += rating;
    }

    public void setRatingValue(int rating) {
        this.rating = rating;
        commentInfo.ecnt += 1;
        commentInfo.eval += rating;
    }


    public static class TrainingCommentInfo implements Serializable {
        @Expose
        @SerializedName("rid")
        String rid;
        @Expose
        @SerializedName("mcnt")
        int mcnt;
        @Expose
        @SerializedName("ccnt")
        int ccnt;
        @Expose
        @SerializedName("ecnt")
        int ecnt;
        @Expose
        @SerializedName("eval")
        int eval;

        public String getRid() {
            return rid;
        }
    }
}
