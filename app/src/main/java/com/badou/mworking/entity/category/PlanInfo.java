package com.badou.mworking.entity.category;

// 需要传递到资源详情页的数据，如标题，当前时间，总时间等
public class PlanInfo {
    public String planTitle;
    public int currentTimeSecond;
    public int maxTimeMinute;

    public PlanInfo(String planTitle, int currentTimeSecond, int maxTimeMinute) {
        this.planTitle = planTitle;
        this.currentTimeSecond = currentTimeSecond;
        this.maxTimeMinute = maxTimeMinute;
    }
}
