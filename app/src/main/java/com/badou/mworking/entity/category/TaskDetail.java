package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

public class TaskDetail implements Serializable {
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
