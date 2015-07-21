package com.badou.mworking.entity.category;

import android.content.Context;

import com.badou.mworking.util.SP;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Classification {
    @Expose
    @SerializedName("name")
    String name;  //分类名称
    @Expose
    @SerializedName("tag")
    int tag;   //分类tag
    @Expose
    @SerializedName("priority")
    int priority;  //分类优先级
    @Expose
    @SerializedName("son")
    List<Classification> son;

    public String getName() {
        return name;
    }

    public int getTag() {
        return tag;
    }

    public int getPriority() {
        return priority;
    }

    public List<Classification> getSon() {
        return son;
    }

    public boolean hasSon() {
        return son != null && son.size() > 0;
    }
}
