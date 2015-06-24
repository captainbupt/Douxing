package com.badou.mworking.net;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/6/24 0024.
 */
public class BaseNetEntity {
    @SerializedName(Net.CODE)
    @Expose
    int errcode;
    @SerializedName(Net.DATA)

}
