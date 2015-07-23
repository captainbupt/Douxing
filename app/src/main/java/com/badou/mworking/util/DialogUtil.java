package com.badou.mworking.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;

import com.badou.mworking.R;
import com.badou.mworking.entity.main.NewVersion;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.view.BaseView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

public class DialogUtil {
    /**
     * 在主页验证是否有软件更新
     *
     * @param newVersion
     */
    public static void apkUpdate(final Context context, final BaseView baseView, final NewVersion newVersion) {
        // 有遮罩则不提示更新
        if (newVersion.hasNewVersion()) {
            new AlertDialog.Builder(context).setTitle(R.string.main_tips_update_title).setMessage(newVersion.getDescription())
                    .setPositiveButton(R.string.main_tips_update_btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    baseView.showProgressDialog(R.string.action_update_download_ing);
                                    ServiceProvider.doUpdateMTraning(context, newVersion.getUrl(), new RangeFileAsyncHttpResponseHandler(new File("update.apk")) { // 仅仅是借用该接口

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, File file) {
                                            baseView.hideProgressDialog();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                            baseView.showToast(R.string.error_service);
                                            baseView.hideProgressDialog();
                                        }
                                    });
                                }
                            }).setNegativeButton(R.string.text_cancel, null).show();
        }
    }
}
