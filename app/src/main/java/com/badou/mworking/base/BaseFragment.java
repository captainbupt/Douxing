package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

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
}
