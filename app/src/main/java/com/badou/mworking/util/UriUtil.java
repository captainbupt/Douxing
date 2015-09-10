package com.badou.mworking.util;

import android.net.Uri;
import android.text.TextUtils;

public class UriUtil {
    public static Uri getHttpUri(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("https://") && !url.startsWith("http://"))
                url = "http://" + url;
            return Uri.parse(url);
        }
        return null;
    }
}
