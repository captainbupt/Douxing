package com.badou.mworking.factory;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 主页面图标按钮工厂类
 */
public class MainGridFactory {
    private static List<MainIcon> mMainIconList;
    private static Context mContext;

    public MainGridFactory(Context context){
        mContext = context;
        mMainIconList = getMainIconList();
    }

    public MainIcon getMainIconByPosition(int position) {
        if (position < 0 || position >= mMainIconList.size()) {
            return null;
        }
        return mMainIconList.get(position);
    }

    public int getCount(){
        return mMainIconList.size();
    }

    /**
     * 功能描述:初始化MainIcon的数据
     */
    private List<MainIcon> getMainIconList() {
        List<MainIcon> mainIconList = new ArrayList<>();

        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_NOTICE, R.drawable.button_notice, R.string.module_default_title_notice));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_TRAIN, R.drawable.button_training, R.string.module_default_title_training));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_EXAM, R.drawable.button_exam, R.string.module_default_title_exam));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_TASK, R.drawable.button_task, R.string.module_default_title_task));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_SURVEY, R.drawable.button_survey, R.string.module_default_title_survey));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_CHATTER, R.drawable.button_chatter, R.string.module_default_title_chatter));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_SHELF, R.drawable.button_shelf, R.string.module_default_title_shelf));
        mainIconList.add(getMainIcon(RequestParams.CHK_UPDATA_PIC_ASK, R.drawable.button_ask, R.string.module_default_title_ask));

        /**
         * 权限， 设置隐藏显示
         * @param access 后台返回的十进制权限制
         */
        int access = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().getAccess();

        char[] accessArray = Integer.toBinaryString(access).toCharArray();
        for (int i = accessArray.length-1; i >= 0 ; i--) {
            if(accessArray[i] == '0')
                mainIconList.remove(i);
        }

        Collections.sort(mainIconList, new Comparator<MainIcon>() {
            @Override
            public int compare(MainIcon t1, MainIcon t2) {
                return Integer.valueOf(t1.getPriority()) > Integer.valueOf(t2.getPriority()) ? 1 : -1;
            }
        }); //对list进行排序
        Collections.reverse(mainIconList);      // 对list集合进行反向
        return mainIconList;
    }

    /**
     * @param key               icon键值
     * @param resId             本地图片
     * @param defaultTitleResId 默认名称
     */
    private MainIcon getMainIcon(String key, int resId, int defaultTitleResId) {
        JSONObject mainIconJSONObject = getMainIconJSONObject(key);
        String title = mainIconJSONObject.optString("name");
        String priority = mainIconJSONObject.optString("priority");
        if (TextUtils.isEmpty(title)) {
            title = mContext.getResources().getString(defaultTitleResId);
        }
        MainIcon mainIcon = new MainIcon(key, resId, title, priority);
        return mainIcon;
    }

    /**
     * 功能描述: 更新数据库中mainIcon的name 字段和 priority 字段
     */
    private JSONObject getMainIconJSONObject(String key) {
        String shuffleStr = SP.getStringSP(mContext, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, "");
        if (TextUtils.isEmpty(shuffleStr)) {
            return null;
        }
        try {
            JSONObject shuffle = new JSONObject(shuffleStr);
            Iterator it = shuffle.keys();
            while (it.hasNext()) {
                String IconKey = (String) it.next();
                if (key.equals(IconKey)) {
                    return shuffle.optJSONObject(IconKey);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
