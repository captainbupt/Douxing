package com.badou.mworking.widget;

import android.content.Context;
import android.view.LayoutInflater;

import com.badou.mworking.R;

import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;

/**
 * Created by Administrator on 2015/5/27.
 */
public class SearchLinearView extends LinearLayout {

    private Context mContext;

    public SearchLinearView(Context context) {
        super(context);
        this.mContext = context;
        setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_activity_main_search, this);
    }
}
