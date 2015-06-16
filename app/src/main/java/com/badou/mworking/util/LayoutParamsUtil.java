package com.badou.mworking.util;

import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015/6/16.
 */
public class LayoutParamsUtil {

    public static ViewGroup.LayoutParams getLayoutParams(ViewGroup parentView, int width, int height) {
        if (parentView.getClass().equals(LinearLayout.class)) {
            return new LinearLayout.LayoutParams(width, height);
        } else if (parentView.getClass().equals(RelativeLayout.class)) {
            return new RelativeLayout.LayoutParams(width, height);
        } else if (parentView.getClass().equals(FrameLayout.class)) {
            return new FrameLayout.LayoutParams(width, height);
        } else if (parentView.getClass().equals(AbsListView.class)) {
            return new AbsListView.LayoutParams(width, height);
        }
        return new ViewGroup.LayoutParams(width, height);
    }
}
