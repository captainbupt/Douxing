package com.badou.mworking.entity.category;

import com.google.gson.annotations.Expose;

public class TrainingCommentInfo {
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
