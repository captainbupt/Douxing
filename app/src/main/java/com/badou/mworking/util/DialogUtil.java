package com.badou.mworking.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.ClipboardManager;

import com.badou.mworking.R;

/**
 * Created by Administrator on 2015/6/8.
 */
public class DialogUtil {
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

    public static void showDeleteDialog(Context context, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle(R.string.tip_delete_confirmation)
                .setPositiveButton(R.string.text_ok, listener).setNegativeButton(R.string.text_cancel, null).show();
    }

}
