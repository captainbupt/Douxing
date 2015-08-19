package com.badou.mworking.entity.category;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.util.GsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * 功能描述: 任务签到实体类
 */
public class Task extends Category {

    @Expose
    @SerializedName("photo")
    int photo;// 是否上传照片
    @Expose
    @SerializedName("qrint")
    int qrint; // 是否支持二维码
    @Expose
    @SerializedName("startline")
    long startline;// 开始时间
    @Expose
    @SerializedName("deadline")
    long deadline;// 结束时间
    @Expose
    @SerializedName("longitude")
    double longitude;// 经度
    @Expose
    @SerializedName("latitude")
    double latitude;// 纬度
    @Expose
    @SerializedName("place")
    String place;// 显示的地址
    @Expose
    @SerializedName("comment")
    String comment;// 描述
    @SerializedName("content")
    @Expose
    String contentStr;

    transient Content content;

    @Override
    public void updateData(CategoryDetail categoryDetail) {
        this.store = categoryDetail.store;
        this.read = categoryDetail.getContent().c;
    }

    @Override
    public int getCategoryType() {
        return Category.CATEGORY_TASK;
    }

    @Override
    public boolean isOffline() {
        return super.isOffline() || getDeadline() < Calendar.getInstance().getTimeInMillis();
    }

    public boolean isFreeSign() {
        return latitude == 0 || longitude == 0;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isPhoto() {
        return photo == 1;
    }

    public long getStartline() {
        return startline * 1000l;
    }

    public long getDeadline() {
        return deadline * 1000l;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getPlace() {
        return place;
    }

    @Override
    public String getImg() {
        return img;
    }

    public String getComment() {
        return comment;
    }

    public String getPhotoUrl() {
        return getContent().getP();
    }

    private Content getContent() {
        if (content == null) {
            if (TextUtils.isEmpty(contentStr)) {
                content = new Content();
            } else {
                content = GsonUtil.fromJson(contentStr, Content.class);
            }
        }
        return content;
    }

    static class Content {
        @Expose
        @SerializedName("p")
        String p;

        public String getP() {
            return p;
        }
    }

}
