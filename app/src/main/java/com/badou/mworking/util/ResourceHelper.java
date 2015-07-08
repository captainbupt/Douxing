package com.badou.mworking.util;

import android.app.Application;
import android.content.res.Resources;

public class ResourceHelper {
    private static Resources resources;

    public static void init(Application application) {
        resources = application.getResources();
    }

    public static String getString(int resId) {
        return resources.getString(resId);
    }
}
