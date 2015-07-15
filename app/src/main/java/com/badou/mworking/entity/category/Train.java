package com.badou.mworking.entity.category;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 功能描述: 培训实体类
 */
public class Train extends Category{

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


    public static class TrainingCommentInfo implements Serializable{
        @Expose
        String rid;
        @Expose
        int mcnt;
        @Expose
        int ccnt;
        @Expose
        int ecnt;
        @Expose
        int eval;

        public String getRid() {
            return rid;
        }

        public int getMcnt() {
            return mcnt;
        }

        public int getCcnt() {
            return ccnt;
        }

        public int getEcnt() {
            return ecnt;
        }

        public int getEval() {
            return eval;
        }

        public void setEcnt(int ecnt) {
            this.ecnt = ecnt;
        }

        public void setEval(int eval) {
            this.eval = eval;
        }

        public void setCcnt(int ccnt) {
            this.ccnt = ccnt;
        }
    }
}
