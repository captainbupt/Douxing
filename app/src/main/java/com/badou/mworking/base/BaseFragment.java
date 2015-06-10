package com.badou.mworking.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

/**
 * Created by Administrator on 2015/6/10.
 */
public class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;
    protected Bundle mReceivedArguments;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mReceivedArguments = getArguments();
    }
}
