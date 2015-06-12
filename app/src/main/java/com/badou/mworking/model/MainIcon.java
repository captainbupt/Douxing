package com.badou.mworking.model;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public final static Map<String, MainIcon> mainIconMap = new HashMap<>(8);

    public static MainIcon getMainIcon(Context context, String key) {
        MainIcon mainIcon = mainIconMap.get(RequestParameters.CHK_UPDATA_PIC_ASK);
        if (mainIcon != null)
            return mainIcon;

        if (key.equals(RequestParameters.CHK_UPDATA_PIC_ASK)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_ASK, R.drawable.button_ask, R.string.module_default_title_ask);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_SHELF)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_SHELF, R.drawable.button_shelf, R.string.module_default_title_shelf);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_SURVEY)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_SURVEY, R.drawable.button_survey, R.string.module_default_title_survey);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_CHATTER)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_CHATTER, R.drawable.button_chatter, R.string.module_default_title_chatter);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_TASK)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TASK, R.drawable.button_task, R.string.module_default_title_task);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_EXAM)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_EXAM, R.drawable.button_exam, R.string.module_default_title_exam);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_TRAINING)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_TRAINING, R.drawable.button_training, R.string.module_default_title_training);
        } else if (key.equals(RequestParameters.CHK_UPDATA_PIC_NOTICE)) {
            mainIcon = getMainIcon(context, RequestParameters.CHK_UPDATA_PIC_NOTICE, R.drawable.button_notice, R.string.module_default_title_notice);
        }
        mainIconMap.put(key, mainIcon);
        return mainIcon;
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
