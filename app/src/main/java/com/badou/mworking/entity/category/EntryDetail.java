package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Calendar;

public class EntryDetail implements Serializable {
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

    public void incrementEnroll() {
        enroll++;
    }

    public void decrementEnroll() {
        enroll--;
    }

    public int getIn() {
        return in;
    }

    public EntryContent getContent() {
        return content;
    }

    public static class EntryContent implements Serializable {
        @SerializedName("0")
        String description;

        public String getDescription() {
            return new String(Base64.decodeBase64(description.getBytes()), Charset.forName("UTF-8"));
        }
    }
}
