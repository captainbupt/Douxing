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

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryDetail {

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

    transient Content content;

    static class Content{
        int e = -1;
        int c;

        public int getE() {
            return e;
        }

        public int getC() {
            return c;
        }
    }

    static class Entry{
        int offline;
        int maxusr;
        long deadline;
        long startline;
        long deadline_c;
        long startline_c;
        int enroll;
        int in;
        EntryContent content;

        public int getOffline() {
            return offline;
        }

        public int getMaxusr() {
            return maxusr;
        }

        public long getDeadline() {
            return deadline;
        }

        public long getStartline() {
            return startline;
        }

        public long getDeadline_c() {
            return deadline_c;
        }

        public long getStartline_c() {
            return startline_c;
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

    static class EntryContent{
        @SerializedName("0")
        String description;

        public String getDescription() {
            return description;
        }
    }

    static class Task{
        int offline;
        String place;
        float latitude;
        float longitude;
        String comment;
        long deadline;
        long startline;
        int photo;
        int qrint;

        public int getOffline() {
            return offline;
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
            return deadline;
        }

        public long getStartline() {
            return startline;
        }

        public int getPhoto() {
            return photo;
        }

        public int getQrint() {
            return qrint;
        }
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
