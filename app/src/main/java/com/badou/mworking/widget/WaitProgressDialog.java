package com.badou.mworking.widget;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;

import com.badou.mworking.R;


public class WaitProgressDialog extends ProgressDialog {

    private WeakReference<Context> mContext;

    public WaitProgressDialog(Context context, String msg) {
        super(context);
        this.mContext = new WeakReference<Context>(context);
        init(msg);

    }

    public WaitProgressDialog(Context context, int resId) {
        super(context);
        this.mContext = new WeakReference<Context>(context);
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
        if (mContext.get() != null) {
            setContent(msg);
            setTitle(mContext.get().getString(R.string.message_tips));
            setCanceledOnTouchOutside(false);
        }
    }

    public void setContent(String msg) {
        if (mContext.get() != null) {
            if (TextUtils.isEmpty(msg)) {
                setMessage(mContext.get().getString(R.string.message_wait));
            } else {
                setMessage(msg);
            }
        }
    }

    public void setContent(int msgId) {
        if (mContext.get() != null) {
            try {
                setContent(mContext.get().getResources().getString(msgId));
            } catch (NotFoundException e) {
                setContent(mContext.get().getResources().getString(R.string.message_wait));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void show() {
        if (!((Activity) mContext.get()).isFinishing())
            super.show();
    }

    @Override
    public void dismiss() {
        if (!((Activity) mContext.get()).isFinishing())
            super.dismiss();
    }
}
