package com.badou.mworking.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.badou.mworking.R;

/**
 * Created by Administrator on 2015/6/10.
 */
public class DeleteClickListener implements View.OnClickListener {

    private Context mContext;
    public DialogInterface.OnClickListener mOnPositiveClickListener;

    public DeleteClickListener(Context context, DialogInterface.OnClickListener onPositiveClickListener) {
        this.mContext = context;
        this.mOnPositiveClickListener = onPositiveClickListener;
    }

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(mContext).setTitle(R.string.tip_delete_confirmation)
                .setPositiveButton(R.string.text_ok, mOnPositiveClickListener).setNegativeButton(R.string.text_cancel, null).show();
    }
}
