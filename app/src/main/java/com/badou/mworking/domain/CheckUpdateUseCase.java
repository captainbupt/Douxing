package com.badou.mworking.domain;

import android.content.Context;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.DensityUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class CheckUpdateUseCase extends UseCase {

    private Context mContext;

    public CheckUpdateUseCase(Context context) {
        this.mContext = context;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().checkUpdate(UserInfo.getUserInfo().getUid(), getScreenLevel(mContext), new Body());
    }

    /**
     * 功能描述: 获取屏幕级别
     */
    public String getScreenLevel(Context context) {
        int screenWidthPx = DensityUtil.getInstance().getScreenWidth();
        //适配240 320 480 屏幕
        if (screenWidthPx >= 240 && screenWidthPx < 720 - 100) {
            return "sm";
            //适配中等密度 720
        } else if (screenWidthPx >= 720 - 100 && screenWidthPx < 1080 - 100) {
            return "md";
            //适配1080
        } else if (screenWidthPx >= 1080 - 100) {
            return "lg";
            // 默认给定中屏尺寸
        } else {
            return "md";
        }
    }

    public static class Body {
        @SerializedName("button_vlogo")
        String button_vlogo = "";
        @SerializedName("banner")
        String banner = "";
        @SerializedName("newver")
        String newver = "";

    }
}
