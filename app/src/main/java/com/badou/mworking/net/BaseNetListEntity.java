package com.badou.mworking.net;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class BaseNetListEntity<T> {

    @SerializedName(Net.CODE)
    @Expose
    int errcode;
    @SerializedName(Net.DATA)
    @Expose
    List<T> data;

    public static BaseNetListEntity fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(BaseNetListEntity.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public String toJson(Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = type(BaseNetListEntity.class, clazz);
        return gson.toJson(this, objectType);
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

}
