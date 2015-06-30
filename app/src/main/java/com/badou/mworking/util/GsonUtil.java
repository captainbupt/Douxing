package com.badou.mworking.util;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2015/6/24 0024.
 */
public class GsonUtil {
    private static Gson gson;

    public static void initialize(){
        gson = new Gson();
    }

    public static <T> T fromJson(String jsonString, Class<T> className) {
        return gson.fromJson(jsonString, className);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    // Collect需要使用此方法
    public static String toJson(Object object, Type type) {
        return gson.toJson(object, type);
    }
}
