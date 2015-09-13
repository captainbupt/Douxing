package com.badou.mworking.entity.category;

import android.text.TextUtils;

import com.badou.mworking.entity.StoreItem;
import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryDetail implements Serializable, StoreItem {

    @SerializedName("mcnt")
    int mcnt;
    @SerializedName("ccnt")
    int ccnt;
    @SerializedName("ecnt")
    int ecnt;
    @SerializedName("eval")
    int eval;
    @SerializedName("content")
    String contentStr;
    @SerializedName("url")
    String url;
    @SerializedName("fmt")
    int fmt;
    @SerializedName("tag")
    String tag;
    @SerializedName("store")
    boolean store;
    @SerializedName("subject")
    String subject;
    @SerializedName("img")
    String img;
    @SerializedName("link_to")
    String link_to;
    @SerializedName("share_url")
    String shareUrl;
    @SerializedName("entry")
    EntryDetail entry;
    @SerializedName("task")
    TaskDetail task;
    @SerializedName("plan")
    PlanDetail plan;

    // 为了保证在传递categoryDetail的过程中，对content的修改不丢失，所以给他添加一个字段。
    // 主要是由于contentStr的格式错误才会有现在这个麻烦
    @SerializedName("contentclass")
    Content content;

    public static class Content implements Serializable {
        @SerializedName("e")
        int e = -1;
        @SerializedName("c")
        int c; // 是否签到
        @SerializedName("p")
        String p; // 签到图片

        public boolean isSigned() {
            return c == 1;
        }

        public void setSigned(boolean isSigned) {
            this.c = isSigned ? 1 : 0;
        }

        public String getImgUrl() {
            return p;
        }
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setCcnt(int ccnt) {
        this.ccnt = ccnt;
    }

    public void setEcnt(int ecnt) {
        this.ecnt = ecnt;
    }

    public TaskDetail getTask() {
        return task;
    }

    public EntryDetail getEntry() {
        return entry;
    }

    public PlanDetail getPlan() {
        return plan;
    }

    public int getRating() {
        return getContent().e;
    }

    public void setRating(int rating) {
        getContent().e = rating;
    }

    public void setStore(boolean store) {
        this.store = store;
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

    public String getUrl() {
        return url;
    }

    public int getFmt() {
        return fmt;
    }

    public String getTag() {
        return tag;
    }

    public boolean isStore() {
        return store;
    }

    public String getSubject() {
        return subject;
    }

    public String getImg() {
        return img;
    }

    public String getLink_to() {
        return link_to;
    }

    public Content getContent() {
        if (content == null) {
            if (TextUtils.isEmpty(contentStr)) {
                content = new Content();
            } else {
                content = GsonUtil.fromJson(contentStr, Content.class);
            }
        }
        return content;
    }
}
