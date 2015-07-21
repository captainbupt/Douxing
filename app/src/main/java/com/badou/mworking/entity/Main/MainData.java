package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainData {
    @Expose
    @SerializedName("newver")
    NewVersion newver;
    @Expose
    @SerializedName("button_vlogo")
    Logo button_vlogo;
    @Expose
    @SerializedName("banner")
    List<MainBanner> banner;

    public NewVersion getNewVersion() {
        return newver;
    }

    public Logo getButton_vlogo() {
        return button_vlogo;
    }

    public List<MainBanner> getBanner() {
        return banner;
    }
}
