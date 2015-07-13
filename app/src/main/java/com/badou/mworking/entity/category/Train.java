package com.badou.mworking.entity.category;

import com.google.gson.annotations.Expose;

/**
 * 功能描述: 培训实体类
 */
public class Train extends Category {

    transient boolean isTraining = true;

    @Expose
    TrainingCommentInfo commentInfo;
    @Expose
    int rating;

    public Train() {
        commentInfo = new TrainingCommentInfo();
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
        return commentInfo.getCcnt();
    }

    public void setCommentNumber(int number) {
        commentInfo.setCcnt(number);
    }

    public int getRatingNumber() {
        return commentInfo.getEcnt();
    }

    public int getRatingTotalValue() {
        return commentInfo.getEval();
    }

    public int getRating() {
        return rating;
    }

    public void setRatingValue(int rating, int number) {
        this.rating = rating;
        commentInfo.setEcnt(number);
        commentInfo.setEval(commentInfo.getEval() + rating);
    }

    public void setRatingValue(int rating) {
        this.rating = rating;
        commentInfo.setEcnt(commentInfo.getEcnt() + 1);
        commentInfo.setEval(commentInfo.getEval() + rating);
    }
}
