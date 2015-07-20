package com.badou.mworking.entity.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Calendar;

public class CategoryDetail implements Serializable {

    int mcnt;
    int ccnt;
    int ecnt;
    int eval;
    @SerializedName("content")
    String contentStr;
    String url;
    int fmt;
    String tag;
    boolean store;
    String subject;
    String img;
    String link_to;
    Entry entry;
    Task task;

    // 为了保证在传递categoryDetail的过程中，对content的修改不丢失，所以给他添加一个字段。
    // 主要是由于contentStr的格式错误才会有现在这个麻烦
    @SerializedName("contentclass")
    Content content;

    public static class Content implements Serializable {
        int e = -1;
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
        int offline;
        int maxusr;
        long deadline;
        long startline;
        long deadline_c;
        long startline_c;
        int enroll;
        int in;
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

    public static class Task implements Serializable {
        int offline;
        String place;
        float latitude;
        float longitude;
        String comment;
        long deadline;
        long startline;
        int photo;
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
