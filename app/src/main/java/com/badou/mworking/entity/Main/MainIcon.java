package com.badou.mworking.entity.Main;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.net.RequestParameters;
import com.google.gson.annotations.Expose;

import java.util.Comparator;

/**
 * 功能描述:主页的icon
 */
public class MainIcon{

    @Expose
    private String priority; // 模块的优先级，级别越高，在主页面中显示的越前面，该字段在登录时返回，默认最后一个是更多，倒数第二个是个人中心，所以没有这两个的级别返回
    @Expose
    private String name;//item显示的名称

    private int resId;//图片的url

    // 空的构造函数，给GSON使用
    public MainIcon() {
    }

    public MainIcon(String priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }
}
