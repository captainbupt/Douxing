package com.badou.mworking.listener;

import android.content.Context;
import android.view.View;

public abstract class AdapterItemLongClickListener implements View.OnLongClickListener {
    protected Context mContext;

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }

    protected int mPosition;

    public AdapterItemLongClickListener(Context context) {
        this.mContext = context;
    }
}
