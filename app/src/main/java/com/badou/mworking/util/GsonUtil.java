package com.badou.mworking.util;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonUtil {
    private static Gson gson;

    public static void initialize() {
        gson = new Gson();
    }

    public static Object fromJson(String jsonString, Type type) {
        return gson.fromJson(jsonString, type);
    }

    public static <T> List<T> fromJsonList(String jsonString, Class<T> className) {
        List<LinkedTreeMap> list = gson.fromJson(jsonString, new TypeToken<List<LinkedTreeMap>>() {
        }.getType());
        return fromLinedTreeMap(list, className);
    }

    public static <T> List<T> fromLinedTreeMap(List<LinkedTreeMap> list, Class<T> className) {
        List<T> newList = new ArrayList<>();
        for (LinkedTreeMap tmp : list) {
            newList.add(fromJson(toJson(tmp), className));
        }
        return newList;
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
