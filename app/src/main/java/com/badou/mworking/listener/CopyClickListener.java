package com.badou.mworking.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.ClipboardManager;
import android.view.View;

import com.badou.mworking.R;
import com.badou.mworking.util.ToastUtil;

/**
 * Created by Administrator on 2015/6/10.
 */
public class CopyClickListener implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    public String content;

    public CopyClickListener(Context context) {
        this.mContext = context;
    }

    public CopyClickListener(Context context, String content) {
        this.mContext = context;
        this.content = content;
    }

    @Override
    public void onClick(View view) {
        showCopyDialog(mContext, content);
    }

    @Override
    public boolean onLongClick(View view) {
        showCopyDialog(mContext, content);
        return true;
    }

    public static void showCopyDialog(final Context context, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.dialog_operation)
                .setItems(new String[]{context.getResources().getString(R.string.dialog_operation_copy)},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ClipboardManager clip = (ClipboardManager) context
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setText(content); //
                                // 复制
                                ToastUtil.showToast(context, R.string.dialog_operation_copy_success);
                            }
                        }).show();
    }
}
