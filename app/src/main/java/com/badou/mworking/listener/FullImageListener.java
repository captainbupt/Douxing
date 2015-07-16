package com.badou.mworking.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.badou.mworking.PhotoActivity;

/**
 * Created by Administrator on 2015/6/8.
 */
public class FullImageListener implements View.OnClickListener {

    private String mUrl;
    private Context mContext;

    public FullImageListener(Context context, String url) {
        this.mUrl = url;
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_URL, mUrl);
        mContext.startActivity(intent);
    }
}
