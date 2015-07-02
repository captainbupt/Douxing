package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/6/10.
 */
public class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = activity;
    }

/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            MobclickAgent.onPageStart(getClass().getName()); //统计页面
        }else{
            MobclickAgent.onPageEnd(getClass().getName());
        }
    }*/
}
