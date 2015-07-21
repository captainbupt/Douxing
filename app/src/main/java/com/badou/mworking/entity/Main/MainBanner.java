package com.badou.mworking.entity.main;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 功能描述: 主页banner
 */
public class MainBanner {
    @Expose
    @SerializedName("img")
    String img;      // banner的图片url地址
    @Expose
    @SerializedName("url")
    String url;  //点击banner进入的url地址
    @Expose
    @SerializedName("md5")
    String md5;        //banner的MD5值

    public MainBanner(String img, String url) {
        this.img = img;
        this.url = url;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }
}
