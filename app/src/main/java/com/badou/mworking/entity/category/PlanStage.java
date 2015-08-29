package com.badou.mworking.entity.category;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlanStage implements Serializable {
    @SerializedName("phase")
    int phase;
    @SerializedName("subject")
    String subject;
    @SerializedName("desc")
    String description;
    @SerializedName("link")
    List<String> link;

    public int getPhase() {
        return phase;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        if (TextUtils.isEmpty(description))
            return "暂无描述";
        return description;
    }

    public List<String> getLink() {
        return link;
    }
}
