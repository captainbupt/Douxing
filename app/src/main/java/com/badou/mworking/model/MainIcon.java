package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * 功能描述:主页的icon
 */
public class MainIcon {

    public String mainIconId = "";//模块
    public int resId = -1;//图片的url
    public String priority = "0"; // 模块的优先级，级别越高，在主页面中显示的越前面，该字段在登录时返回，默认最后一个是更多，倒数第二个是个人中心，所以没有这两个的级别返回
    public String name = "";//item显示的名称

    public MainIcon(String mainIconId, int url, String name, String priority) {
        this.mainIconId = mainIconId;
        this.name = name;
        this.resId = url;
        this.priority = priority;
    }

    /**
     * @param key               icon键值
     * @param resId             本地图片
     * @param defaultTitleResId 默认名称
     */
    public static MainIcon getMainIcon(Context context, String key, int resId, int defaultTitleResId) {
        JSONObject mainIconJSONObject = getMainIconJSONObject(context, key);
        if (mainIconJSONObject == null)
            return new MainIcon(key, resId, context.getResources().getString(defaultTitleResId), "1");
        String title = mainIconJSONObject.optString("name");
        String priority = mainIconJSONObject.optString("priority");
        if (TextUtils.isEmpty(title)) {
            title = context.getResources().getString(defaultTitleResId);
        }
        return new MainIcon(key, resId, title, priority);
    }

    /**
     * 功能描述: 更新数据库中mainIcon的name 字段和 priority 字段
     */
    private static JSONObject getMainIconJSONObject(Context context, String key) {
        JSONObject shuffle = ((AppApplication) context.getApplicationContext()).getUserInfo().shuffleStr;
        Iterator it = shuffle.keys();
        while (it.hasNext()) {
            String IconKey = (String) it.next();
            if (key.equals(IconKey)) {
                return shuffle.optJSONObject(IconKey);
            }
        }
        return null;
    }

}
