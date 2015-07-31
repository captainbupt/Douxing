package com.badou.mworking.entity.category;

import android.text.TextUtils;

import com.badou.mworking.entity.StoreItem;
import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Calendar;

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
    @SerializedName("entry")
    Entry entry;
    @SerializedName("task")
    Task task;

    @SerializedName("plan")//
    Plan plan;

    // 为了保证在传递categoryDetail的过程中，对content的修改不丢失，所以给他添加一个字段。
    // 主要是由于contentStr的格式错误才会有现在这个麻烦
    @SerializedName("contentclass")
    Content content;

    public static class Content implements Serializable {
        @SerializedName("e")
        int e = -1;
        @SerializedName("c")
        int c;

        public int getScore() {
            return e;
        }

        public boolean isSigned() {
            return c == 1;
        }

        public void setSigned(boolean isSigned) {
            this.c = isSigned ? 1 : 0;
        }
    }

    public static class Entry implements Serializable {
        @SerializedName("offline")
        int offline;
        @SerializedName("maxusr")
        int maxusr;
        @SerializedName("deadline")
        long deadline;
        @SerializedName("startline")
        long startline;
        @SerializedName("deadline_c")
        long deadline_c;
        @SerializedName("startline_c")
        long startline_c;
        @SerializedName("enroll")
        int enroll;
        @SerializedName("in")
        int in;
        @SerializedName("content")
        EntryContent content;

        public void setIn(int in) {
            this.in = in;
        }

        public boolean isOffline() {
            return offline == 1 || getDeadline() < Calendar.getInstance().getTimeInMillis();
        }

        public boolean isStarted() {
            return getStartline() < Calendar.getInstance().getTimeInMillis();
        }

        public int getMaxusr() {
            return maxusr;
        }

        public long getDeadline() {
            return deadline * 1000l;
        }

        public long getStartline() {
            return startline * 1000l;
        }

        public long getDeadline_c() {
            return deadline_c * 1000l;
        }

        public long getStartline_c() {
            return startline_c * 1000l;
        }

        public int getEnroll() {
            return enroll;
        }

        public int getIn() {
            return in;
        }

        public EntryContent getContent() {
            return content;
        }
    }

    public static class EntryContent implements Serializable {
        @SerializedName("0")
        String description;

        public String getDescription() {
            return new String(Base64.decodeBase64(description.getBytes()), Charset.forName("UTF-8"));
        }
    }


    /**
     * 学习计划
     */
    public static class Plan implements Serializable {

        @SerializedName("content")
        PlanContent content;
    public PlanContent getContent() {
        return content;
    }
}
    public static class PlanContent implements Serializable {
    @SerializedName("0")
    String description;//计划简介
    public String getDescription() {
        return new String(Base64.decodeBase64(description.getBytes()), Charset.forName("UTF-8"));
    }
}






    public static class Task implements Serializable {
        @SerializedName("offline")
        int offline;
        @SerializedName("place")
        String place;
        @SerializedName("latitude")
        float latitude;
        @SerializedName("longitude")
        float longitude;
        @SerializedName("comment")
        String comment;
        @SerializedName("deadline")
        long deadline;
        @SerializedName("startline")
        long startline;
        @SerializedName("photo")
        int photo;
        @SerializedName("qrint")
        int qrint;

        public boolean isFreeSign() {
            return latitude == 0 || longitude == 0;
        }

        public boolean isOffline() {
            return offline == 1 || getDeadline() < Calendar.getInstance().getTimeInMillis();
        }

        public boolean isQrint() {
            return qrint > 0;
        }

        public boolean isPhoto() {
            return photo == 1;
        }

        public String getPlace() {
            return place;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public String getComment() {
            return comment;
        }

        public long getDeadline() {
            return deadline * 1000l;
        }

        public long getStartline() {
            return startline * 1000l;
        }

        public int getPhoto() {
            return photo;
        }

        public int getQrint() {
            return qrint;
        }
    }

    public void setCcnt(int ccnt) {
        this.ccnt = ccnt;
    }

    public void setEcnt(int ecnt) {
        this.ecnt = ecnt;
    }

    public Task getTask() {
        return task;
    }

    public Entry getEntry() {
        return entry;
    }

    public Plan getPlan() {
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
