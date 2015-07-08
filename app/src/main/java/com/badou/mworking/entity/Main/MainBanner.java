package com.badou.mworking.entity.main;


import com.google.gson.annotations.Expose;

/**
 * 功能描述: 主页banner
 */
public class MainBanner {
    @Expose
    String img;      // banner的图片url地址
    @Expose
    String url;  //点击banner进入的url地址
    @Expose
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
