package com.badou.mworking.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.badou.mworking.ChattingActivity;
import com.badou.mworking.base.BaseActionBarActivity;

public class MessageClickListener implements View.OnClickListener {

    private Context mContext;
    public String userName;
    public String whom;
    public String headUrl;

    public MessageClickListener(Context context) {
        this.mContext = context;
    }

    public MessageClickListener(Context context, String userName, String whom, String headUrl) {
        this.mContext = context;
        this.userName = userName;
        this.whom = whom;
        this.headUrl = headUrl;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, ChattingActivity.class);
        // intent.putExtra(BaseActionBarActivity.KEY_TITLE, userName);
        intent.putExtra(ChattingActivity.KEY_WHOM, whom);
        intent.putExtra(ChattingActivity.KEY_OTHER_IMG, headUrl);
        mContext.startActivity(intent);
    }
}
