package com.badou.mworking.net;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseOpenNetEntity<T> {
    @SerializedName("code")
    int errcode;
    @SerializedName(Net.DATA)
    T data;

    public BaseOpenNetEntity() {
    }

    public BaseOpenNetEntity(int errcode, T data) {
        this.errcode = errcode;
        this.data = data;
    }

    public static BaseOpenNetEntity fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(BaseOpenNetEntity.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public String toJson(Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = type(BaseOpenNetEntity.class, clazz);
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

    public int getErrcode() {
        return errcode;
    }

    public T getData() {
        return data;
    }
}
