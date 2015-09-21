package com.badou.mworking.util;

import android.net.Uri;
import android.text.TextUtils;

import com.badou.mworking.R;

public class UriUtil {
    public static Uri getHttpUri(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("https://") && !url.startsWith("http://"))
                url = "http://" + url;
            return Uri.parse(url);
        }
        return null;
    }

    public static Uri getResourceUri(int resId){
        return Uri.parse("android.resource://com.badou.mworking/" + resId);
    }
}
