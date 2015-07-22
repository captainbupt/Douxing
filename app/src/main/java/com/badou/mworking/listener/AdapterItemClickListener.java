package com.badou.mworking.listener;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2015/6/15.
 */
public abstract class AdapterItemClickListener implements View.OnClickListener {
    protected Context mContext;

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }

    int mPosition;

    public AdapterItemClickListener(Context context) {
        this.mContext = context;
    }
}
