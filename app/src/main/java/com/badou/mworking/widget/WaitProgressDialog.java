package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;

import com.badou.mworking.R;

import org.holoeverywhere.app.ProgressDialog;

public class WaitProgressDialog extends ProgressDialog {

    private Context mContext;

    public WaitProgressDialog(Context context, String msg) {
        super(context);
        this.mContext = context;
        init(msg);

    }

    public WaitProgressDialog(Context context, int resId) {
        super(context);
        this.mContext = context;
        try {
            init(context.getResources().getString(resId));
        } catch (NotFoundException e) {
            init(context.getResources().getString(R.string.message_wait));
            e.printStackTrace();
        }
    }

    public WaitProgressDialog(Context context) {
        this(context, context.getResources().getString(R.string.message_wait));
    }

    private void init(String msg) {
        setContent(msg);
        setTitle(mContext.getString(R.string.message_tips));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setContent(String msg){
        if (TextUtils.isEmpty(msg)) {
            setMessage(mContext.getString(R.string.message_wait));
        } else {
            setMessage(msg);
        }
    }

    public void setContent(int msgId){
        try {
            setContent(mContext.getResources().getString(msgId));
        } catch (NotFoundException e) {
            setContent(mContext.getResources().getString(R.string.message_wait));
            e.printStackTrace();
        }
    }

}
