package com.badou.mworking.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.badou.mworking.MultiPhotoActivity;

import java.util.ArrayList;

public class FullImageListener implements View.OnClickListener {

    private String mUrl;
    private Context mContext;

    public FullImageListener(Context context, String url) {
        this.mUrl = url;
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
        Intent intent = MultiPhotoActivity.getIntentFromWeb(mContext, new ArrayList<String>() {{
            add(mUrl);
        }}, 0);
        mContext.startActivity(intent);
    }
}
