package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainData {
    @SerializedName("newver")
    NewVersion newver;
    @SerializedName("button_vlogo")
    Logo button_vlogo;
    @SerializedName("button_vlogin")
    Logo button_vlogin;
    @SerializedName("button_vflash")
    Logo button_vflash;
    @SerializedName("banner")
    List<MainBanner> banner;
    @SerializedName("credit")
    Credit credit;

    public NewVersion getNewVersion() {
        return newver;
    }

    public Logo getButton_vlogo() {
        return button_vlogo;
    }

    public Logo getButton_vlogin() {
        return button_vlogin;
    }

    public Logo getButton_vflash() {
        return button_vflash;
    }

    public List<MainBanner> getBanner() {
        return banner;
    }

    public int getDayAct() {
        return credit.dayact;
    }

    static class Credit {
        @SerializedName("dayact")
        int dayact;
    }
}
