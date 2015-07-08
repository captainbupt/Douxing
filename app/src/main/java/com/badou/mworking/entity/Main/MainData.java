package com.badou.mworking.entity.main;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MainData {
    @Expose
    NewVersion newver;
    @Expose
    Logo button_vlogo;
    @Expose
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
